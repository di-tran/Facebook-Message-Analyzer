# Facebook Message Analyzer

What is it?
-------------
Facebook Message Analyzer parses a user's Facebook messages and generates metrics
based off the received data. It specifically parses the messages.htm file obtained by
downloading an archive of a user's Facebook data. The analyzer does not 
connect online and only parses htm files.

How to Use
-------------
First, obtain a copy of your Facebook information. As of 1/19/2016, instructions
on how to download a Facebook archive can be found here:
https://www.facebook.com/help/131112897028467/

Once downloaded, unzip the archive and extract the messages.htm. This file contains all
messages sent by the user, and is the main file that Facebook Message Analyzer will
work with. Place this file in the root directory of the source code, or use this file's
path when referencing it in the API's load method. 

Modules
-------------
FBAnalyzer - The workhorse module. Responsible for parsing and interpreting data.

FBThread - Data module containing FBMessages and helper methods.

FBMessage - Data module containing message text & metadata, along with helper methods.

Known Bugs
-------------
Some methods have not yet been implemented. Such methods are marked in the documentation
and should not be called until a further version updates this.

Authors
------------
Di Tran

