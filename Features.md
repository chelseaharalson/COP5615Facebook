Facebook is a complex and diverse platform. There are hundreds of object and edge types. This file distills the project requirements to a more manageable set.

# Required Features
* A user must be able to register under a username
* Users must be able to be friends with other users
* Albums with actual pictures must be able to be created
* A page must be able to be created and queried
* Posts, pages, and photos must be able to be liked
* Users and pages can post to their own wall or others' walls
* Comments can be made on posts, albums, and photos
* Users must be able to discover new objects to interact with
	- This could be a newsfeed mechanism, where all of the user's friends / liked pages will display recently interacted objects

# Required Objects
* Profile
	- Acts as a simple router between a User or Page object when provided with an ID
	This allows for clients to fetch metadata about either one and determine the actual type
* User
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
* Page
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
	- Place Type:
		- Restaurant
		- Shop
		- Landmark
		- Business
		- Park
* Post
	- ID
	- Created Time
	- From
	- Message
	- Privacy
	- Shares
	- Likes
	- Last 5 people that liked post
	- Comments
* Friend List
	- Array of User IDs who are friends with the target User ID
* Album
	- ID
	- Number of photos
	- Cover Photo
	- Created Time
	- Description
	- From
	- Name
	- Likes
	- Comments
	- Array of Photos
* Picture
	- ID
	- Caption
	- Width
	- Height
	- Likes
	- Album
	- Created Time
	- Comments
	- Data
		- Base64 raw picture data over JSON
		- Could be a small icon or gravatar-like picture
* Comment
	- ID
	- From commenter
	- Comment text
	- Targets
		- Posts
		- Pages
		- Photos
* Comment List (comment thread)
	- Associated Object ID
	- Array of comments

# Optional Objects
* Newsfeed
	- Objects
* Likes
	- Posts
	- Albums
	- Photos
	- Comments
* Notification
	- ID
	- Description
	- Object
* Messages
	- ID
	- From
	- To
	- Message
