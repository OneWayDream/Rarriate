/*---------------------------------------------------------------------------*\
# `INFORMATION ABOUT TCP TYPES`

## *0 - 9 : service types*

### 0 : re-query in case of fcs distortion

###### order - message id

### 1-2 : registration frames

#### (1) client - contains user nickname and UDP address info
###### order - message id, client udp-get address and player data.

#### (2) server - contains info about player on this server
###### order - message id, client id, server udp get address, server id and world info

### 3 : server answer that player with such nickname already exist

### 4 : server says that new player was connected
###### order - message id, player data

### 5 : client break-abstractBlock message
###### order - message id, abstractBlock data

### 6 : server break-abstractBlock message
###### order - message id, abstractBlock data

### 7 : client abstractBlock-place message
###### order - message id, abstractBlock data

### 8 : server abstractBlock-place message
###### order - message id, abstractBlock data

### 9 : client chat-message
###### order - message id, message text

### 10 : server chat-message
###### order - message id, sender-nickname and message text

/*---------------------------------------------------------------------------*\

# `INFORMATION ABOUT UDP TYPES`

### 0 : client - player move frame 

###### order - client id, newX, newY

### 1: server - player move frame
###### order - message id, nickname, newX, newY

/*---------------------------------------------------------------------------*\
