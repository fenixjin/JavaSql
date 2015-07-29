create table user_info
(
	userID int IDENTITY(0,1) primary key,
	username varchar(26) NOT NULL,
	user_pwd varchar(18) NOT NULL,
	reliability DECIMAL(3,2) ,
	deviceID varchar(20),
	response_count int default(0),
	check (user_pwd LIKE '______%'),
)
	
create table request
(
	starterID	int	NOT NULL,
	requestID	int	IDENTITY(0,1) primary key,
	request_info varchar(140),
	request_type int 	NOT NULL,
	starttime	datetime,
	deadline	datetime	NOT NULL,
	longitude	double precision	NOT NULL,
	latitude	double precision	NOT NULL,
	response_num 	int default 0,
	foreign key (starterID) REFERENCES user_info(userID)
)

create table response
(
	requestID int NOT NULL,
	responseID	int IDENTITY(0,1) primary key,
	reliability decimal,
	item_num	int,
	answerID int NOT NULL,
	itemID0	int,
	itemID1 int,
	itemID2 int,
	itemID3 int,
	itemID4 int,
	itemID5 int,
	itemID6 int,
	itemID7 int,
	itemID8 int,
	itemID9 int,
	item0_score	int,
	item1_score	int,
	item2_score int,
	item3_score int,
	item4_score int,
	item5_score int,
	item6_score int,
	item7_score int,
	item8_score int,
	item9_score int,
	foreign key (requestID) REFERENCES request(requestID),
)

create table item_stat
(
	itemID 	int,
	item_category int,
	requestID	int,
	recommender_count	int default(0),
	total_recommendation	int default(0),
	average	int,
	recommendation_score int,
	context_score int,
	primary key (itemID,  item_category, requestID),
)


