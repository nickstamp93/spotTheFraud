This is a semester assignment for a class in our university (Aristotle University of Thessaloniki).
The purpose is to find suspicious user activity in the Twitter such as bots etc.
The process is divided in some steps . 
The first step is to collect tweets for some period of time (we did it for 4 days).Each tweet is under one or more 
TREND (topic or hashtag name).
After that gathering of trends and topics we have our basic data set . For the saving of the data we use the MongoDB .
Based on this data set we procced on the next step . We divide users in 4 categories based on their reference frequency
of the subjects gathered in the previous step . So the first categorha has users with not many references in the top 
trends and the last category contains the most suspicious , theoretically , users with many references . 
We go on and randomly select 10 users from each category . After that we surveil the activity for those 40 users only 
for the next 3 days . After that surveilance , we exclude some characteristics for those users and based on the values
for different characteristics between users from different categories , we try to find suspicious accounts.
At the end , we construct scatter plots for the characteristics in order to see visually the difference , if any, 
between the accounts .
