/*---------------------------------------------------------------------------*\
# `INFORMATION ABOUT TCP TYPES`

## *0 - 9 : service types*

### 0 : re-query in case of fcs distortion

###### order - message id

### 1-2 : registration frames

#### (1) client - contains user nickname and UDP address info
###### order - message id, username, client udp-get address and player data.

#### (2) server - contains info about player on this server
###### order - message id, client id, server udp get address and server id

### 3 : server answer that player with such nickname already exist

/*---------------------------------------------------------------------------*\

# `INFORMATION ABOUT UDP TYPES`

### 0 : client - player move frame

###### order - client id, newX, newY

### 1: server - player move frame
###### order - message id, nickname, newX, newY

/*---------------------------------------------------------------------------*\
