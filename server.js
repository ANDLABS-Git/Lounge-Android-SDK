#!/bin/env node

// Copyright (c) 2013 All Right Reserved, 
// Author: Robert Weindl
// E-Mail: robert.weindl@blackstack.net

//  lounge-server v.0.0.1
var express 	= require('express');
var fs 			= require('fs');
var http		= require('http');
var mongoose	= require('mongoose');
var util 		= require('util');

/**
 * 	Include mongodb schemes.
 */
var Schema 			= mongoose.Schema;  
var User 			= require('./models/user');
var Match			= require('./models/match')
 
/**
 * 	HTTP Error codes.
 */
 var httpStatusCodeOK		= 200;
 var httpErrorBadRequest 	= 400;
 var httpErrorUnauthorized 	= 401;

/**
 *  Implementation of the LoungeServer Application.
 */
var LoungeServer = function() {
 
    //  Scope.
    var self = this;
 
    /*  ================================================================  */
    /*  Server Environment & Helper functions  							  */
    /*  ================================================================  */
 
    /**
     *  Set up server IP address and port # using env variables/defaults.
     */
    self.setupVariables = function() {
		// mongodbAddress for nodejitsu:
        self.mongodbAddress = 'mongodb://nodejitsu:4226bafd5bb734c192f0700b7b2e114c@linus.mongohq.com:10092/nodejitsudb7687973685';
		//self.mongodbAddress = 'mongodb://127.0.0.1:27017/lounge'
    };
 
 
    /**
     *  Populate the cache.
     */
    self.populateCache = function() {
        if (typeof self.zcache === "undefined") 
		{
            self.zcache = { 'test.html': '' };
        }
 
        //  Local cache for static content.
        // self.zcache['test.html'] = fs.readFileSync('./test.html');
    };
 
 
    /**
     *  Retrieve entry (content) from cache.
     *  @param {string} key  Key identifying content to retrieve from cache.
     */
    self.cache_get = function(key) { return self.zcache[key]; };
 
 
    /**
     *  terminator === the termination handler
     *  Terminate server on receipt of the specified signal.
     *  @param {string} sig  Signal to terminate on.
     */
    self.terminator = function(sig)
	{
        if (typeof sig === "string") 
		{
           console.log('%s: Received %s - terminating sample app ...',
                       Date(Date.now()), sig);
           process.exit(1);
        }
        console.log('%s: Node server stopped.', Date(Date.now()) );
    };
 
 
    /**
     *  Setup termination handlers (for exit and a list of signals).
     */
    self.setupTerminationHandlers = function()
	{
        //  Process on exit and signals.
        process.on('exit', function() { self.terminator(); });
 
        // Removed 'SIGPIPE' from the list - bugz 852598.
        ['SIGHUP', 'SIGINT', 'SIGQUIT', 'SIGILL', 'SIGTRAP', 'SIGABRT',
         'SIGBUS', 'SIGFPE', 'SIGUSR1', 'SIGSEGV', 'SIGUSR2', 'SIGTERM'
        ].forEach(function(element, index, array) 
		{
            process.on(element, function() { self.terminator(element); });
        });
    };
 
    /*  ================================================================  */
    /*  App server functions (main app logic here).                       */
    /*  ================================================================  */
 
    /**
     *  Error handler.
     */
     self.handleError = function(err) 
	 {
        // TODO: Implement error handling.
        console.error(err);
     };


    /**
     *  Method to remove loaded routes. 
     */
    self.removeRoute = function(route) {};


    /**
     *  Create the public GET routing table entries + handlers for the application.
     */
    self.createPublicGetRoutes = function() 
	{
        self.getPublicRoutes = { };

		self.getPublicRoutes['/'] = function (req, res) 
		{
			res.render('index.html');
		};	
		
    };
	
	
    /**
     *  Create the public POST routing table entries + handlers for the application.
     */
    self.createPublicPostRoutes = function() 
	{
        self.postPublicRoutes = { };
    };


    /**
     *  Create the private GET routing table entries + handlers for the application.
     */
    self.createPrivateGetRoutes = function() 
	{
        self.getPrivateRoutes = { };
    };
 

    /**
     *  Create the private POST routing table entries + handlers for the application.
     */
    self.createPrivatePostRoutes = function() 
	{
        self.postPrivateRoutes = { };
 
        self.postPrivateRoutes['/private/roger'] = function(req, res) 
		{
            res.send("Secret");
        };
    };
 
 
    /**
     *  Initialize the database (mongoose) and create the database connection.
     */
    self.initializeDatabase = function() 
	{
        // Create database connection.
        mongoose.connect(self.mongodbAddress);
 
        // Configure the mongoose callbacks.
        self.db = mongoose.connection;
        self.db.on('error', console.error.bind(console, 'connection error:'));
        self.db.once('open', function callback () {
            console.log('Created connection to the database ...');
        });
    };
	
	
	/**
	 *	Validates a parameter.
	 */
    self.validateParameter = function(value)
    {
   		return ("undefined" === typeof value) ? "" : value;
    };
	
	
 	/**
	 *	Initialize the socket.io functionality.
	 */
	self.initializeSocketIO = function() 
	{
		// Setup the socket.io instance variable.
		self.io = require('socket.io').listen(self.http, { log: true });
		
		// Initialize the socket routes.
		self.io.sockets.on('connection', function (socket) 
		{	
			/**
			 *	Handle a login.
			 */		
			socket.on('login', function(payload)
			{
				if ('' === self.validateParameter(payload))
				{
					socket.emit('login', { result: false, description: "No payload sent." });
					return;
				}
				
				var data = {playerID: payload.playerID, password: payload.playerID}
				
				// Authenticate the user.
				User.authenticate(data, socket, function(err, user)
				{
					if (err)
					{
						socket.emit('login', { result: false, description: err }); 
						return;
					}
				
					if (user)
					{						
						User.onlineUsers(function(err, onlineUsers)
						{
							if (err)
							{
								socket.emit('login', { result: false, description: err });
								return;
							}
							
							if (onlineUsers)
							{
								// Send the new user all online users.
								socket.emit('login', { result: true, playerList: onlineUsers });
							
								// Send the new user all open games.
								Match.allMatches(function (err, allMatches)
								{
									if (err) 
									{
										// TODO: Handle Error!
									}
									
									if (allMatches)
									{										
										allMatches.forEach(function (match)
										{
											socket.emit('joinMatch', { gameID: match.gameID, matchID: match._id, gameName: match.gameName, totalSpots: match.maximumPlayers, status: match.status, playerIDs: match.participants })
										});
									}
								});
								
								// Send the new authenticated user to all already online users.						
								onlineUsers.forEach(function (u)
								{
									// The new user should not 
									if (socket.id !== u.socketID)
									{
										// Identify the socket for a specific user.
										var s = self.io.sockets.sockets[u.socketID];
								
										// Validate the socket.
										if (!('undefined' === typeof s))
										{
											// Send the chat message to the user.
											s.emit('addPlayer', { playerID: user.playerID });
										}
									}
								});
							}
							else
							{
								socket.emit('login', { result: false, description: "Cannot find online user." });
								return;
							}
						});
					} 
					else
					{					
						// Inform the user that the authentication was not successfull.
						socket.emit('login', { result: false, description: "There was a problem in the login functionality." });
					}
				});
			});
			
			/**
			 *	Handle a logout.
			 */
			socket.on('logout', function() 
			{
				socket.disconnect();
			});
			
			/**
			 *	Handle the message delivery.
			 */
			socket.on('chat', function(payload)
			{
				// Validate the message.
				if ('' === self.validateParameter(payload) ||
					'' === self.validateParameter(payload.message))
				{
					socket.emit('chat', { result: false, description: 'No payload sent.' });
					return;
				}
				
				// Extract the chat message.
				var message = payload.message;
				
				// Identify the user sending the message.
				User.userForSocket(socket, function(err, user)
				{
					if (err) 
					{ 
						socket.emit('chat', { result: false, description: err }); 
						return;
					}
					
					if (user)
					{
						// Iterate all online user and send the message.
						User.onlineUsers(function(err, onlineUsers)
						{
					
							if (err)
							{
								socket.emit('chat', { result: false, description: err });
								return;
							}
							else
							{
								socket.emit('chat', { result: true });
							}
					
							onlineUsers.forEach(function (u)
							{
								// Identify the socket for a specific user.
								var s = self.io.sockets.sockets[u.socketID];
								
								// Validate the socket.
								if (!('undefined' === typeof s))
								{
									// Send the chat message to the user.
									s.emit('addChatMessage', { playerID: user.playerID, message: message });
								}
							});
						});
					}
					else
					{
						return socket.emit('chat', { result: false, description: "There was a problem in the chat functionality." });
					}
				});
			});
			
			/**
			 *	Handle a match create or join.
			 */
			socket.on('join', function(payload)
			{
				// Validate the payload.
				if ('' !== self.validateParameter(payload))
				{					
					// Determine if the user creates a match or joins a match.
					if ('' !== self.validateParameter(payload.gameID))
					{
						// Identify the user sending the message.
						User.userForSocket(socket, function(err, user)
						{
							if (err)
							{
								socket.emit('join', { result: false, description: err });
								return;
							}
							
							// The user could be identfied.
							if (user)
							{
								// Find the correct join mechanism.
								if ('' !== self.validateParameter(payload.gameName) &&
									'' !== self.validateParameter(payload.maximumPlayers))
								{
									// Create a new match.
									Match.create(payload, user._id, function(err, match)
									{
										if (err)
										{
											socket.emit('join', { result: false, description: err });
											return;
										}
										
										if (match)
										{											
											// Iterate all online user and inform about the match creation.
											User.onlineUsers(function(err, onlineUsers)
											{
					
												if (err)
												{
													socket.emit('join', { result: false, description: err });
													return;
												}
												else
												{
													// Give the socket a valid callback.
													socket.emit('join', { result: true, description: "create" });									
					
													console.log(match);				
					
													// Inform all online player about the match creation.
													onlineUsers.forEach(function (u)
													{
														// Identify the socket for a specific user.
														var s = self.io.sockets.sockets[u.socketID];
								
														// Validate the socket.
														if (!('undefined' === typeof s))
														{
															// Send the chat message to the user.
															s.emit('joinMatch', { gameID: match.gameID, matchID: match._id, gameName: match.gameName, totalSpots: match.maximumPlayers, status: match.status, playerIDs: match.participants });
														}
													});
												}
											});											
										}
										else
										{
											socket.emit('join', { result: false, description: 'Cannot create match.' });
											return;
										}
									});
								}
								else if ('' !== self.validateParameter(payload.matchID))
								{
									// Join a match.
									Match.join(payload, user._id, function(err, match)
									{
										if (err)
										{
											socket.emit('join', { result: false, description: err });
											return;
										}
										
										if (match)
										{
											// Iterate all online user and inform about the match creation.
											User.onlineUsers(function(err, onlineUsers)
											{
					
												if (err)
												{
													socket.emit('join', { result: false, description: err });
													return;
												}											
												else
												{
													// Give the socket a valid callback.
													socket.emit('join', { result: true, description: "join" });											
																									
													// Inform all online player about the match creation.
													onlineUsers.forEach(function (u)
													{
														// Identify the socket for a specific user.
														var s = self.io.sockets.sockets[u.socketID];
								
														// Validate the socket.
														if (!('undefined' === typeof s))
														{
															// Send the chat message to the user.
															s.emit('joinMatch', { gameID: match.gameID, matchID: match._id, gameName: match.gameName, totalSpots: match.maximumPlayers, status: match.status, playerIDs: match.participants });
														}
													});
												}
											});												
										}
										else
										{
											socket.emit('join', { result: false, description: 'Cannot join match.' });
											return;
										}
									});
								}
								else
								{
									socket.emit('join', { result: false, description: 'Cannot determine the correct join mechanism.'});
									return;
								}
							}
							else
							{
								socket.emit('join', { result: false, description: 'Cannot identify the user creating the game.' });
								return;
							}
						});
					}
					else
					{
						socket.emit('join', { result: false, description: 'No gameID was specified.' });
						return;
					}
				}
				else
				{
					socket.emit('join', { result: false, description: 'No payload sent.' });
					return;
				}
			});
			
			/**
			 *	Handle a match move.
			 */
			socket.on('move', function(payload)
			{
				// Validate the payload.
				if ('' !== self.validateParameter(payload))
				{
					// Determine if the user creates a match or joins a match.
					if ('' !== self.validateParameter(payload.gameID) &&
						'' !== self.validateParameter(payload.matchID) &&
						'' !== self.validateParameter(payload.move))
					{
						// Identify the user sending the update.
						User.userForSocket(socket, function(err, user)
						{
							if (err)
							{
								socket.emit('move', { result: false, description: err });
								return;
							}
							else
							{
								if (user)
								{
									Match.update(payload, user._id, function(err, match)
									{
										if (err) 
										{
											socket.emit('move', { result: false, description: err });
										}
										else
										{
											if (match)
											{
												socket.emit('move', { result: true });
											
												// Inform all online player about the new match status.
												onlineUsers.forEach(function (u)
												{
													// Identify the socket for a specific user.
													var s = self.io.sockets.sockets[u.socketID];
						
													// Validate the socket.
													if (!('undefined' === typeof s))
													{
														// Send the chat message to the user.
														s.emit('moveMatch', { gameID: match.gameID, matchID: match._id, status: match.move });
													}
												});
											}
											else
											{
												socket.emit('move', { result: false, description: 'Cannot update the match move.' });
												return;
											}
										}
									});							
								}
								else
								{
									socket.emit('move', { result: false, description: 'Cannot determine user.' });
									return;									
								}
							}
						});
					} 
					else
					{
						socket.emit('move', { result: false, description: 'No gameID, matchID or move was specified.' });
						return;
					}
				}
				else
				{
					socket.emit('move', { result: false, description: 'No payload sent.' });
					return;
				}
			});
			
			/**
			 *	Handle a match status update.
			 */
			socket.on('update', function(payload)
			{
				// Validate the payload.
				if ('' !== self.validateParameter(payload))
				{
					// Determine if the user creates a match or joins a match.
					if ('' !== self.validateParameter(payload.gameID) &&
						'' !== self.validateParameter(payload.matchID) &&
						'' !== self.validateParameter(payload.status))
					{
						// Identify the user sending the update.
						User.userForSocket(socket, function(err, user)
						{
							if (err)
							{
								socket.emit('update', { result: false, description: err });
								return;
							}
							else
							{
								if (user)
								{
									Match.update(payload, user._id, function(err, match)
									{
										if (err) 
										{
											socket.emit('update', { result: false, description: err });
										}
										else
										{
											if (match)
											{
												socket.emit('update', { result: true });
											
												// Inform all online player about the new match status.
												onlineUsers.forEach(function (u)
												{
													// Identify the socket for a specific user.
													var s = self.io.sockets.sockets[u.socketID];
						
													// Validate the socket.
													if (!('undefined' === typeof s))
													{
														// Send the chat message to the user.
														s.emit('updateMatch', { gameID: match.gameID, matchID: match._id, status: match.status });
													}
												});
											}
											else
											{
												socket.emit('update', { result: false, description: 'Cannot update the match status.' });
												return;
											}
										}
									});							
								}
								else
								{
									socket.emit('update', { result: false, description: 'Cannot determine user.' });
									return;									
								}
							}
						});
					} 
					else
					{
						socket.emit('update', { result: false, description: 'No gameID, matchID or status was specified.' });
						return;
					}
				}
				else
				{
					socket.emit('update', { result: false, description: 'No payload sent.' });
					return;
				}
			});
			
			/**
			 *	Handle a user checkIn.
			 */
			socket.on('checkIn', function(payload)
			{
				// Validate the payload.
				if ('' !== self.validateParameter(payload))
				{
					// Determine if the user creates a match or joins a match.
					if ('' !== self.validateParameter(payload.gameID) &&
						'' !== self.validateParameter(payload.matchID))
					{
						// Identify the user sending the update.
						User.checkIn(payload, socket, function(err, user)
						{
							if (err)
							{
								socket.emit('checkIn', { result: false, description: err });
								return;
							}
							else
							{
								if (user)
								{
									socket.emit('checkIn', { result: true });
									
									// Inform all online player about the new match status.
									onlineUsers.forEach(function (u)
									{
										// Identify the socket for a specific user.
										var s = self.io.sockets.sockets[u.socketID];
			
										// Validate the socket.
										if (!('undefined' === typeof s))
										{
											// Send the chat message to the user.
											s.emit('updateCheckIn', { gameID: user.gameID, matchID: user.matchID, playerID: user.playerID });
										}
									});
								}
								else
								{
									socket.emit('update', { result: false, description: 'Cannot update the user checkIn.' });
									return;					
								}
							}
						});
					} 
					else
					{
						socket.emit('checkIn', { result: false, description: 'No gameID or matchID was specified.' });
						return;
					}
				}
				else
				{
					socket.emit('checkIn', { result: false, description: 'No payload sent.' });
					return;
				}
			});
			
			/**
			 *	Handle a disconnect.
			 */
			socket.on('disconnect', function() 
			{
				// Deauthenticate the user.
				User.deauthenticate(socket, function(err, user)
				{					
					if (user)
					{						
						// Iterate all online user and inform about the logout of an user.
						User.onlineUsers(function(err, onlineUsers)
						{
							onlineUsers.forEach(function (u)
							{
								var socket = self.io.sockets.sockets[u.socketID];
								
								// Validate the socket.
								if (!('undefined' === typeof s))
								{
									socket.emit('delPlayer', { playerID: user.playerID });
								}
							});
						});					
					}
				});				
			});
		});
	}
	

    /**
     *  Middleware to authenticate against the server with the selected oauth2 server.
     */
    self.authenticate = function() 
	{
        return function(req, res, next) 
		{
			next();
        };  
    };


    /**
     *  Initialize the server (express) and create the routes and register
     *  the handlers.
     */
    self.initializeServer = function() 
	{
        // Initialize all route arrays.
        self.createPrivateGetRoutes();
        self.createPrivatePostRoutes();
        self.createPublicGetRoutes();
        self.createPublicPostRoutes();

        // Initialization of express.
        self.app = express();

        // Configuration of express
        self.app.configure(function() 
		{
            self.app.use(express.bodyParser());
			self.app.use(express.static(__dirname + '/public'));

        });
 
        // Configure the SSL server
        self.http = http.createServer(self.app);
 
        // Add public GET handlers for the app without authentication (from the getRoutes).
        for (var g in self.getPublicRoutes) 
		{
            self.app.get(g, self.getPublicRoutes[g]);
        }

        // Add public POST handlers for the app without authentication (from the postRoutes).  
        for (var p in self.postPublicRoutes) 
		{
            self.app.post(p, self.postPublicRoutes[p]);
        }

        //  Add private GET handlers for the app with authentication (from the getPrivateRoutes).
        for (var g in self.getPrivateRoutes) 
		{
            self.app.get(g, self.authenticate(), self.getPrivateRoutes[g]);
        }
 
        // Add private POST handlers for the app with authentication (from the postPrivateRoutes).
        for (var p in self.postPrivateRoutes) 
		{
            self.app.post(p, self.authenticate(), self.postPrivateRoutes[p]);
        }
    };
 
 
    /**
     *  Initializes the kardamom-server application.
     */
    self.initialize = function() 
	{
		// Initialize the server environment.
        self.setupVariables();
        self.populateCache();
        self.setupTerminationHandlers();
 
        // Initialize the database connection.
        self.initializeDatabase();

        // Initialize the express server, passport and routes.
        self.initializeServer();

		// Initialize the socket.io services
		self.initializeSocketIO();	
    };
 
 
    /**
     *  Start the server (starts up the kardamom-server application).
     */
    self.start = function() 
	{
		// TODO: MongoDB Reset all user to offline and clear all sockets.		
		
        // Start the app on the specific interface (and port).
        self.http.listen(8080, "127.0.0.1", function() 
		{
            //console.log(8080, "127.0.0.1", '%s: Node server started...');
            console.log('%s: Node server started on %s:%d ...',
                        Date(Date.now() ), self.ipaddress, self.port);
        });
    };
};   /*  LoungeServer */
 
/**
 *  main():  Initialize and start the LoungeServer Application.
 */
var loungeServer = new LoungeServer();
loungeServer.initialize();
loungeServer.start();
