CREATE TABLE links_store
(id VARCHAR(90) PRIMARY KEY,
 url VARCHAR(90),
 user_id VARCHAR(90) references users(id));

--;;

CREATE TABLE tags
(id VARCHAR(90) PRIMARY KEY,
 title VARCHAR(90) UNIQUE);

--;;

CREATE TABLE links_tags_rel
(id VARCHAR(90) PRIMARY KEY,
 link_id VARCHAR(90) references links_store(id),
 tag_id VARCHAR(90) references tags(id));
