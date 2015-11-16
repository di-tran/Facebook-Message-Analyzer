# Facebook-Message-Analyzer

What is it?
-------------
Facebook Message Analyzer parses Facebook messages and generates statistics
based off them. It specifically parses the messages.htm file obtained by
downloading an archive of a user's Facebook data. The analyzer does not 
connect online and only parses htm files.

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

