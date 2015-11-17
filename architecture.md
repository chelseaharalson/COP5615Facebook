# Backend Architecture
* ID Structure
* Ents for Access Control
* Keeping security in mind (public private keys)
* Storing Objects (hash tables, in memory databases, etc.)
* Functions to access data

## Creating an account
A password will never be needed to create an account. This is because we
eventually will have a PKI for identifying users. In order to create an account
with this model, a user merely has to request a username and fill in some basic
information. After this point, they will have a session identifying them as
this user.

The most basic information required from a new user. It makes sense to require
all information up-front as the users will be automatically generated.

# Client Architecture
* How security will be eventually integrated
* Client personalities (aggressive, passive, etc.)
* Content Generation (wordlists, random(), etc.)
