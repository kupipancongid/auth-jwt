CREATE TABLE `user` (
    `id` char(36) NOT NULL,
    `email` varchar(255) NOT NULL,
    `password` varchar(255) DEFAULT NULL,
    `token` varchar(255) NOT NULL
);
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);
