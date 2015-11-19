# FEATURES:
* Profile:
	- User
		- ID
		- First Name
		- Last Name
		- Birthday
		- Gender
		- Email
		- About
		- Relationship Status
		- Interested In
		- Political
		- Updated Time
		- Timezone
		- Status
	- Page
		- ID
		- Name
		- About
		- Business
		- Contact Address
		- Cover Photo
		- Description
		- Location
		- Like Count
		- Phone
		- Place Type: Restaurant, Shop, Landmark, Business, Park
* Posts:
	- ID
	- Created Time
	- From
	- Message
	- Privacy
	- Shares
	- Likes
	- Comments
* Friend Lists
* Picture:
	- ID
	- Caption
	- Width
	- Height
	- Likes
	- Album
	- Created Time
	- Comments
	- Note: we should use actual binary pictures over JSON, encoded with base64
* Albums:
	- ID
	- Number of photos
	- Cover Photo
	- Created Time
	- Description
	- From
	- Name
	- Likes
	- Comments
	- Photos
* Comments:
	- Posts
	- Albums
	- Photos
* Comment List:
	- Like Count
	- Last 5 people that liked comment
* Likes:
	- Posts
	- Albums
	- Photos
	- Comments
* Notification:
	- ID
	- Description
	- Object
* Messages:
	- ID
	- From
	- To
	- Message
* Newsfeed:
	- Objects
