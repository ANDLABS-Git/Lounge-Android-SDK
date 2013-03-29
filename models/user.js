// Copyright (c) 2013 All Right Reserved, 
// Author: Robert Weindl
// E-Mail: robert.weindl@blackstack.net

var mongoose 	= require('mongoose');
var Schema = mongoose.Schema;

/**
 * 	UserSchema.
 */
var UserSchema = new Schema
({	
	// The playerID of the user.
    playerID: 	{ type: String, trim: true, default: "" },
	
	// The password of the user.
    password: 	{ type: String, trim: true, default: "" },
	
	// The socket of the user.
	socketID: 	{ type: String, trim: true, default: "" },
	
	// Flag to identify if the user is online
	isOnline: 	{ type: Boolean, trim: true, default: false},
	
	// Current gameID
	gameID: 	{ type: String, trim: true, default: "" },
	
	// Current matchID
	matchID: 	{ type: Schema.Types.ObjectId, ref: 'Match' }
});

/**
 * 	The purpose of the method is to validate an user.
 * 	@param data A json object containing all user relevant information. (playerID, password)
 * 	param socket The socket of the user.
 *  @return callback(error, success)	
 */
UserSchema.statics.authenticate = function(data, socket, callback) 
{
	// Validate data.
	if ('undefined' === typeof data)
	{
		return callback(null, null);
	}
	
	// Extract all required information.
	var playerID = validateParameter(data.playerID);
	var password = validateParameter(data.password);
	var socketID = validateParameter(socket.id);
	
	// Validate the information.
	if ('' === playerID ||
		'' === password ||
		'' === socketID)
	{
		return callback(null, null);
	}
	
	// Validate the standard information.
	if ('playerID' === playerID ||
		'password' === password)
	{
		return callback(null, null);
	}
	
	// Validate or create the person. Update the socket.
	mongoose.models['User'].findOne({ playerID: playerID }, function(err, user)
	{
		if (err) { return callback(err, null); }
		
		// Person exists so validate the person and update the socket.
		if (user)
		{
			// Validate the password.
			if (user.password !== password)
			{
				return callback(null, null);
			}
			else
			{							
				// Update the socket of the person.
				user.socketID = socketID;
				user.isOnline = true;
				
				// Save the changes.
				user.save(function(err)
				{
					if (err) { return callback(err, false); }
					return callback(null, user);
				});
			}
		}
		else
		{
			// Create a new user.
			var user = new User({ playerID: playerID, password: password, socketID: socketID, isOnline: true });
			
			// Save the changes.
			user.save(function(err) 
			{
				if (err) { return callback(err, null); }
				return callback(null, user);
			});
		}
	})
};

/**
 * 	The purpose of the method is to deauthenticate an user and do the logout
 * 	@param socket The socket of the user.
 *  @return callback(error, success)	
 */
UserSchema.statics.deauthenticate = function(socket, callback)
{
	// Extract the required information.
	var socketID = validateParameter(socket.id);
	
	if ('' === socketID)
	{
		return callback(null, null);
	}
	
	// Find the user for the corresponding socket.
	mongoose.models['User'].findOne({ socketID: socketID }, function(err, user)
	{
		if (err) { return callback(err, null); }
		
		if (user)
		{
			user.isOnline = false;
			user.socketID = '';
			user.save(function(err)
			{
				if (err) { return callback(err, null); }
				return callback(null, user);
			});
		}
		else
		{
			return callback(null, null);
		}
	});
};

/**
 *	Receive an online user for a given socket.
 * 	@param socket The socket of the user.
 *	@return callback(error, user)
 */
UserSchema.statics.userForSocket = function(socket, callback)
{
	var socketID = validateParameter(socket.id);
	
	if ('' === socketID)
	{
		return callback(null, null);
	}
	
	mongoose.models['User'].findOne({ socketID: socketID, isOnline: true }, function(err, user)
	{
		if (err) { return callback(err, null); }
		
		if (user)
		{
			return callback(null, user);
		}
		else
		{
			return callback(null, null);
		}
	});
};

/**
 *	Receive an array with socketID's for all user which status is online.
 *	@return callback(error, onlineUsers)
 */
UserSchema.statics.onlineUsers = function(callback)
{
	mongoose.models['User'].find({ isOnline: true }, 'playerID socketID', function(err, userArray)
	{
		if (err) { return callback(err, null); }
		
		if (userArray)
		{
			return callback(null, userArray);	
		}
		else
		{
			return callback(null, null);
		}
	});
};

/**
 *	Keep track about the current location in the lounge application.
 *	@param data A json object containing all user relevant information.
 *	@param socket The socket of the user.
 *  @return callback(error, user)
 */
UserSchema.statics.checkIn = function(data, socket, callback)
{
	if ('' !== validateParameter(data) &&
		'' !== validateParameter(data.gameID) &&
		'' !== validateParameter(data.matchID) &&
		'' !== validateParameter(socket))
	{
		mongoose.models['User'].findOne({ socketID: socketID, isOnline: true }, function(err, user)
		{
			if (err) { return callback(err, null); }
		
			if (user)
			{
				// Store the new location.
				user.gameID = data.gameID;
				user.matchID = data.matchID;
				
				user.save(function(err)
				{
					if (err) { return callback(err, null); }
					return callback(null, user);
				});				
			}
			else
			{
				return callback(null, null);
			}
		});
	}
	else
	{
		return callback(null, null);
	} 
};

/**
 *	Check if a value is undefined.
 * 	@return Value if it is not undefined else an empty string.
 */
 var validateParameter = function(value)
 {
	 return ("undefined" === typeof value) ? "" : value;
 };

/**
 *	 Export the UserSchema to use it as a User class.
 */
var User = mongoose.model('User', UserSchema);
module.exports = User;