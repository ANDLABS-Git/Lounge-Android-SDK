// Copyright (c) 2013 All Right Reserved, 
// Author: Robert Weindl
// E-Mail: robert.weindl@blackstack.net

var mongoose 	= require('mongoose');
var Schema = mongoose.Schema;

/**
 * 	MatchSchema.
 */
var MatchSchema = new Schema
({	
	// matchID === _id (Will be generated automtically.)
		
	// The game packageID.
    gameID: 	{ type: String, trim: true, default: "" },
	
	// The name of the game app.
	gameName: 	{ type: String, trim: true, default: "" },
	
	// The status of the match.
	status: { type: String, trim: true, default: "open" },
	
	// The maximum number of allowed playera in a match.
	maximumPlayers: { type: Number, default: 0},
	
	// Array of all player in the game.
	participants: [{ type: Schema.Types.ObjectId, ref: 'User' }],
	
	// In this version the server is only interested in the last move.
	move: { type: String, trim: true, default: "" },
	
	// Array of all game moves.
	// moves: [{ type: String, trime: true, default: "" }]
});

/**
 *	Creates a new match.
 * 	@param data A json object containing all match relevant information. (gameID, gameName, maximumPlayers)
 *	@param userID The id of the user creating the game.
 *  @return callback(error, match)	
 */
MatchSchema.statics.create = function(data, userID, callback)
{
	if ('' !== validateParameter(data) &&
		'' !== validateParameter(data.gameID) &&
		'' !== validateParameter(data.gameName) &&
		'' !== validateParameter(data.maximumPlayers) &&
		'' !== validateParameter(userID))
	{
		var status = "";
		
		if (data.maximumPlayers > 1)
		{
			status = "join";
		}
		else if (data.maximumPlayers === 1)
		{
			status = "running";
		}
		else
		{
			status = "close";
		}
		
		// Create a new match.
		var match = new Match({ gameID: data.gameID, gameName: data.gameName, status: status, maximumPlayers: data.maximumPlayers });
		
		// Save the new match.
		match.save(function(err)
		{
			if (err) { return callback(err, null); }
			
			// Add the user to the match.
			match.participants.addToSet(userID);
			
			match.save(function(err)
			{
				if (err) { return callback(err, null); }
				mongoose.models['Match'].findOne({ _id: match._id})
				.populate('participants', 'playerID')
				.exec(function(err, match)
				{
					if (err) { return callback(err, null); }
					if (match)
					{
						return callback(null, match);
					}	
					else
					{
						return callback(null, null);
					}
				});
			});
		});
	}
	else
	{
		return callback(null, null);			
	}
};

/**
 *	Allows a user to join a match.
 *	@param data A json object containing all match relevant information. (gameID, matchID)
 *	@param userID The id of the user joining the game.
 *	@return callback(error, match)
 */
MatchSchema.statics.join = function(data, userID, callback)
{
	if ('' !== validateParameter(data) &&
		'' !== validateParameter(data.gameID) &&
		'' !== validateParameter(data.matchID) &&
		'' !== validateParameter(userID)) 
	{
		mongoose.models['Match'].findOne({ gameID: data.gameID, _id: data.matchID }, function(err, match)
		{
			if (err) { return callback(err, null); }
			
			if (match)
			{
				// Check if the game has empty seats.
				if (match.participants.length < match.maximumPlayers)
				{					
					// Validate that user is not already in list!
					containsUserID = false;
					match.participants.forEach(function(uID)
					{
						if (String(uID) === String(userID))
						{
							containsUserID = true;
						}
					});
					
					if (containsUserID)
					{
						return callback(null, null);
					}
					
					// Add the user to the participants.
					match.participants.addToSet(userID);
				
					// Calculate the remaining empty spots
					var emptySpots = match.maximumPlayers - match.participants.length;
				
					// Set the new status.
					if (emptySpots === 0)
					{
						status = "running";
					}
					else if (emptySpots > 0)
					{
						status = "join";
					}
					else
					{
						status = "close";
					}
					match.status = status;
				
					match.save(function(err)
					{
						if (err) { return callback(err, null); }
						mongoose.models['Match'].findOne({ _id: match._id})
						.populate('participants', 'playerID')
						.exec(function(err, match)
						{
							if (err) { return callback(err, null); }
							return callback(null, match);
						});
					});
				}
				else
				{
					return callback(null, null);
				}
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
 *	Allows a user to change the match status.
 *	@param data A json object containing all match relevant information. (gameID, matchID, status)
 *	@param userID The id of the user who is updating the match status.
 *	@return callback(error, match)
 */
MatchSchema.statics.update = function(data, userID, callback)
{
	if ('' !== validateParameter(data) &&
		'' !== validateParameter(data.gameID) &&
		'' !== validateParameter(data.matchID) &&
		'' !== validateParameter(data.status) &&
		'' !== validateParameter(userID)) 
	{
		// Verify a correct status was delivered.
		if (!('open' === payload.status ||
			'running' === payload.status ||
			'close' === payload.status))
		{
			return callback(null, null);
		}
		
		mongoose.models['Match'].findOne({ gameID: data.gameID, _id: data.matchID }, function(err, match)
		{
			if (err) { return callback(err, null); }
			
			if (match)
			{
				// Check if user has the right to update the match status.
				containsUserID = false;
				match.participants.forEach(function(uID)
				{
					if (String(uID) === String(userID))
					{
						containsUserID = true;
					}
				});
		
				if (true === containsUserID)
				{
					match.status = data.status;
					match.save(function(err)
					{
						if (err) { return callback(err, null); }
						mongoose.models['Match'].findOne({ _id: match._id})
						.populate('participants', 'playerID')
						.exec(function(err, match)
						{
							if (err) { return callback(err, null); }
							return callback(null, match);
						});
					});
				}
				else
				{
					return callback(null, null);
				}
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
 *	Allows a user to move.
 *	@param data A json object containing all match relevant information. (gameID, matchID, move)
 *	@param userID The id of the user who is moving.
 *	@return callback(error, match)
 */
MatchSchema.statics.move = function(data, userID, callback)
{
	if ('' !== validateParameter(data) &&
		'' !== validateParameter(data.gameID) &&
		'' !== validateParameter(data.matchID) &&
		'' !== validateParameter(data.move) &&
		'' !== validateParameter(userID)) 
	{
		mongoose.models['Match'].findOne({ gameID: data.gameID, _id: data.matchID }, function(err, match)
		{
			if (err) { return callback(err, null); }
			
			if (match)
			{
				// Check if user has the right to update the match move.
				containsUserID = false;
				match.participants.forEach(function(uID)
				{
					if (String(uID) === String(userID))
					{
						containsUserID = true;
					}
				});
		
				if (true === containsUserID)
				{
					match.move = data.move;
					match.save(function(err)
					{
						if (err) { return callback(err, null); }
						return callback(null, match);
					});
				}
				else
				{
					return callback(null, null);
				}
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
 *	All open games with empty spaces
 *	@return callback(error, emptyGames)
 */
MatchSchema.statics.allMatches = function(callback)
{
	mongoose.models['Match'].find()
	.where('status').in(['join', 'running', 'close'])
	.populate('participants', 'playerID')
	.exec(function(err, allMatches)
	{
		if (err) { return callback(err, null); }
		if (allMatches)
		{
			return callback(null, allMatches);
		}	
		else
		{
			return callback(null, null);
		}
	});

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
 *	 Export the MatchSchema to use it as a Match class.
 */
var Match = mongoose.model('Match', MatchSchema);
module.exports = Match;