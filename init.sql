
INSERT INTO `sys_account` VALUES (91,NULL,0,'2020-02-17 17:51:48',1,NULL,'admin','$2a$10$gPR.Def8xOZUAl4/QDxq8.0nfWJdXgVqzBc9Dlu9K8OyiUoV0tS8m','张三','男',0,NULL),(92,'2020-01-03 01:46:57',0,'2020-02-17 17:51:54',1,NULL,'user','$2a$10$BaIeXyHRjYr9M6DcDlBvC..pBlU2J3RvNlKqNUc0TeCcP1zJJik.a','李四','男',0,NULL);
INSERT INTO `sys_dep` VALUES (1,NULL,0,'2020-02-17 16:43:14',1,'河南省',0,0),(2,NULL,1,'2020-01-04 09:29:14',3,'人力资源部',1,5),(3,NULL,1,NULL,3,'互联网发展部',1,1),(4,NULL,0,'2020-02-17 16:42:51',2,'郑州市',1,0),(5,NULL,0,NULL,3,'UI部',3,0),(6,NULL,0,NULL,3,'开发部',3,1),(11,'2020-01-04 08:37:53',0,NULL,NULL,'aaaa',10,0),(12,'2020-01-04 09:26:06',0,'2020-02-17 16:43:05',2,'新乡市',1,0),(13,'2020-01-04 09:26:25',0,'2020-02-17 16:43:09',2,'开封市',1,0),(14,'2020-02-17 16:43:33',0,NULL,3,'中原区分局',4,0),(15,'2020-02-17 16:43:46',0,NULL,3,'郑东新区分局',4,0),(16,'2020-02-17 16:44:03',0,NULL,3,'金水区分局',4,0),(17,'2020-02-17 16:44:17',0,NULL,2,'市区',12,0),(18,'2020-02-17 16:44:28',0,NULL,3,'班组1',14,0),(19,'2020-02-17 16:44:38',0,NULL,3,'班组2',14,0),(20,'2020-02-17 16:44:45',0,NULL,3,'班组1',15,0);
INSERT INTO `sys_menu` VALUES (2,NULL,0,'2020-01-05 07:50:02',NULL,'fa-cog',0,0,NULL,'系统管理',1),(3,NULL,0,'2020-01-05 09:11:34','','fa-th-large',1,1,'_blank','新车',0),(5,NULL,0,'2020-01-05 08:00:34','','fa-cogs',1,2,'_target','系统管理',0),(7,NULL,0,'2020-01-26 05:09:29','/sys/account/list','fa-user',2,5,'_self','人员管理',1),(8,NULL,0,'2020-01-26 05:09:37','/sys/role/list','fa-group',2,5,'_self','角色管理',2),(9,NULL,0,'2020-01-26 05:09:46','/sys/dep/list','fa-sitemap',2,5,'_self','机构管理',3),(10,NULL,0,'2020-01-26 05:09:58','/sys/menu/list','fa-bars',2,5,'_self','菜单管理',4),(11,NULL,0,'2020-01-21 09:23:59','add',NULL,3,8,NULL,'添加',1),(12,NULL,0,'2020-01-21 09:24:06','edit',NULL,3,8,NULL,'修改',2),(13,NULL,0,'2020-01-21 09:24:19','delete',NULL,3,8,NULL,'删除',4),(14,'2020-01-05 07:52:54',0,'2020-01-05 08:06:05','/clue/list','fa-apple',2,3,'_self','线索列表',0),(17,'2020-01-05 07:54:17',0,'2020-01-05 08:06:13','/cars/list','fa-area-chart',2,3,'_self','车辆库存列表',0),(21,'2020-01-05 08:09:11',0,NULL,NULL,'fa-archive',1,1,NULL,'保险',0),(26,'2020-01-05 14:14:48',0,NULL,NULL,'fa-th-large',1,25,NULL,'11111',0),(28,'2020-01-05 14:15:06',0,NULL,NULL,'fa-th-large',1,27,NULL,'啛啛喳喳',0),(29,'2020-01-08 07:17:05',0,'2020-01-26 05:10:06','/sys/dict/list','fa-th-large',2,5,'_self','字典管理',5),(30,'2020-01-21 09:08:40',0,NULL,'list',NULL,3,8,NULL,'查询',0),(31,'2020-01-21 09:10:14',0,'2020-01-21 09:24:14','menu',NULL,3,8,NULL,'分配权限',3),(32,'2020-01-21 12:31:14',0,'2020-01-21 12:35:55','add',NULL,3,7,NULL,'添加',1),(33,'2020-01-21 12:31:32',0,'2020-01-21 12:36:01','edit',NULL,3,7,NULL,'修改',2),(34,'2020-01-21 12:31:57',0,NULL,'role',NULL,3,7,NULL,'分配角色',3),(35,'2020-01-21 12:32:10',0,'2020-01-21 12:36:08','delete',NULL,3,7,NULL,'删除',4),(36,'2020-01-21 12:32:40',0,'2020-01-21 12:36:56','add',NULL,3,9,NULL,'添加',1),(37,'2020-01-21 12:33:09',0,'2020-01-21 12:37:11','edit',NULL,3,9,NULL,'修改',2),(38,'2020-01-21 12:33:39',0,'2020-01-21 12:37:45','delete',NULL,3,9,NULL,'删除',3),(39,'2020-01-21 12:34:00',0,'2020-01-21 12:38:37','add',NULL,3,10,NULL,'添加',1),(40,'2020-01-21 12:34:09',0,'2020-01-21 12:38:45','edit',NULL,3,10,NULL,'修改',2),(41,'2020-01-21 12:34:20',0,'2020-01-21 12:38:50','delete',NULL,3,10,NULL,'删除',3),(42,'2020-01-21 12:34:42',0,'2020-01-21 12:39:18','add',NULL,3,29,NULL,'添加',1),(43,'2020-01-21 12:34:52',0,'2020-01-21 12:39:23','edit',NULL,3,29,NULL,'修改',2),(44,'2020-01-21 12:35:04',0,'2020-01-21 12:39:27','delete',NULL,3,29,NULL,'删除',3),(45,'2020-01-21 12:35:32',0,NULL,'list',NULL,3,9,NULL,'查询',0),(46,'2020-01-21 12:35:44',0,NULL,'list',NULL,3,7,NULL,'查询',0),(48,'2020-01-21 12:38:00',0,'2020-01-21 12:38:26','list',NULL,3,10,NULL,'查询',0),(49,'2020-01-21 12:39:13',0,NULL,'list',NULL,3,29,NULL,'查询',0),(51,'2020-01-25 14:11:25',0,'2020-01-30 14:03:50','/druid/index.html','fa-th-large',2,5,'_blank','数据库监控',7),(55,'2020-01-26 13:26:46',0,'2020-02-17 17:14:50','/sys/code/list','fa-th-large',2,5,'_self','代码生成',8),(56,'2020-01-26 13:27:02',0,NULL,'list',NULL,3,55,NULL,'查询',0),(57,'2020-01-26 13:29:54',0,'2020-01-26 13:30:02','edit',NULL,3,55,NULL,'编辑',1),(58,'2020-01-26 13:30:20',0,NULL,'delete',NULL,3,55,NULL,'删除',2),(59,'2020-01-26 13:31:14',0,'2020-01-26 13:31:22','generate',NULL,3,55,NULL,'生成代码',3),(60,'2020-01-26 13:32:27',0,NULL,'add',NULL,3,55,NULL,'添加',0),(61,'2020-01-29 06:34:33',0,NULL,NULL,'fa-th-large',1,2,NULL,'测试代码',0),(62,'2020-01-29 06:35:00',0,NULL,'/test/book/list','fa-th-large',2,61,'_self','各项测试',0),(63,'2020-01-29 06:35:16',0,NULL,'list',NULL,3,62,NULL,'查询',0),(64,'2020-01-29 06:35:27',0,NULL,'add',NULL,3,62,NULL,'添加',0),(65,'2020-01-29 06:35:39',0,NULL,'edit',NULL,3,62,NULL,'修改',0),(66,'2020-01-29 06:35:57',0,NULL,'delete',NULL,3,62,NULL,'删除',0),(67,'2020-01-30 14:04:15',0,NULL,'index.html',NULL,3,51,NULL,'详情页',0),(68,'2020-02-17 16:56:44',0,'2020-02-17 17:53:39',NULL,'fa-desktop',0,0,NULL,'业务管理',0),(69,'2020-02-17 17:13:01',0,'2020-02-17 17:14:28','/sys/register/list','fa-th-large',2,5,'_self','注册表管理',6),(70,'2020-02-17 17:13:34',0,NULL,'list',NULL,3,69,NULL,'查询',0),(71,'2020-02-17 17:13:49',0,NULL,'add',NULL,3,69,NULL,'添加',1),(72,'2020-02-17 17:14:02',0,NULL,'edit',NULL,3,69,NULL,'修改',2),(73,'2020-02-17 17:14:18',0,NULL,'delete',NULL,3,69,NULL,'删除',3);
INSERT INTO `sys_register` VALUES (1,'2020-02-17 17:16:36',0,'2020-02-17 17:52:26','系统名称','SYSTEM_TITLE','信息管理');
INSERT INTO `sys_role` VALUES (1,'2020-01-03 08:21:30',0,'2020-01-26 09:30:28','管理员','管理员，具有所有权限','admin'),(2,'2020-01-03 08:21:50',0,'2020-01-26 09:30:08','普通用户','普通用户，只分配查询权限','USER');
INSERT INTO `sys_role_account` VALUES (15,'2020-01-23 16:18:32',0,NULL,91,1),(16,'2020-01-24 07:38:32',0,NULL,114,17),(17,'2020-01-25 03:20:43',0,NULL,115,2),(18,'2020-01-25 05:25:19',0,NULL,117,1),(20,'2020-01-26 02:32:36',0,NULL,119,1),(21,'2020-01-26 02:33:03',0,NULL,118,1),(22,'2020-01-26 04:43:12',0,NULL,122,22),(28,'2020-01-26 05:04:29',0,NULL,92,2),(29,'2020-01-26 05:04:33',0,NULL,124,2),(30,'2020-01-26 08:50:44',0,NULL,126,2);
INSERT INTO `sys_role_menu` VALUES (58,'2020-01-21 09:04:31',0,NULL,1,8),(59,'2020-01-21 09:04:32',0,NULL,3,8),(60,'2020-01-21 09:04:32',0,NULL,14,8),(61,'2020-01-21 09:04:32',0,NULL,17,8),(217,'2020-01-25 03:24:29',0,NULL,2,18),(218,'2020-01-25 03:24:29',0,NULL,5,18),(219,'2020-01-25 03:24:29',0,NULL,7,18),(220,'2020-01-25 03:24:29',0,NULL,46,18),(221,'2020-01-25 03:24:29',0,NULL,32,18),(222,'2020-01-25 03:24:29',0,NULL,33,18),(223,'2020-01-25 03:24:29',0,NULL,34,18),(224,'2020-01-25 03:24:29',0,NULL,35,18),(362,'2020-01-26 08:57:40',0,NULL,2,24),(363,'2020-01-26 08:57:40',0,NULL,5,24),(364,'2020-01-26 08:57:40',0,NULL,7,24),(365,'2020-01-26 08:57:40',0,NULL,46,24),(366,'2020-01-26 08:57:40',0,NULL,32,24),(367,'2020-01-26 08:57:40',0,NULL,33,24),(368,'2020-01-26 08:57:40',0,NULL,34,24),(369,'2020-01-26 08:57:40',0,NULL,35,24),(541,'2020-01-26 10:39:12',0,NULL,2,2),(542,'2020-01-26 10:39:12',0,NULL,5,2),(543,'2020-01-26 10:39:12',0,NULL,7,2),(544,'2020-01-26 10:39:12',0,NULL,46,2),(545,'2020-01-26 10:39:12',0,NULL,8,2),(546,'2020-01-26 10:39:12',0,NULL,30,2),(547,'2020-01-26 10:39:12',0,NULL,9,2),(548,'2020-01-26 10:39:12',0,NULL,45,2),(549,'2020-01-26 10:39:12',0,NULL,10,2),(550,'2020-01-26 10:39:12',0,NULL,48,2),(551,'2020-01-26 10:39:12',0,NULL,29,2),(552,'2020-01-26 10:39:12',0,NULL,49,2),(553,'2020-01-26 10:39:12',0,NULL,51,2),(813,'2020-02-17 17:15:09',0,NULL,2,1),(814,'2020-02-17 17:15:09',0,NULL,5,1),(815,'2020-02-17 17:15:09',0,NULL,7,1),(816,'2020-02-17 17:15:09',0,NULL,46,1),(817,'2020-02-17 17:15:09',0,NULL,32,1),(818,'2020-02-17 17:15:09',0,NULL,33,1),(819,'2020-02-17 17:15:09',0,NULL,34,1),(820,'2020-02-17 17:15:09',0,NULL,35,1),(821,'2020-02-17 17:15:09',0,NULL,8,1),(822,'2020-02-17 17:15:09',0,NULL,30,1),(823,'2020-02-17 17:15:09',0,NULL,11,1),(824,'2020-02-17 17:15:09',0,NULL,12,1),(825,'2020-02-17 17:15:09',0,NULL,31,1),(826,'2020-02-17 17:15:09',0,NULL,13,1),(827,'2020-02-17 17:15:09',0,NULL,9,1),(828,'2020-02-17 17:15:09',0,NULL,45,1),(829,'2020-02-17 17:15:09',0,NULL,36,1),(830,'2020-02-17 17:15:09',0,NULL,37,1),(831,'2020-02-17 17:15:09',0,NULL,38,1),(832,'2020-02-17 17:15:09',0,NULL,10,1),(833,'2020-02-17 17:15:09',0,NULL,48,1),(834,'2020-02-17 17:15:09',0,NULL,39,1),(835,'2020-02-17 17:15:09',0,NULL,40,1),(836,'2020-02-17 17:15:09',0,NULL,41,1),(837,'2020-02-17 17:15:09',0,NULL,29,1),(838,'2020-02-17 17:15:09',0,NULL,49,1),(839,'2020-02-17 17:15:09',0,NULL,42,1),(840,'2020-02-17 17:15:09',0,NULL,43,1),(841,'2020-02-17 17:15:09',0,NULL,44,1),(842,'2020-02-17 17:15:09',0,NULL,69,1),(843,'2020-02-17 17:15:09',0,NULL,70,1),(844,'2020-02-17 17:15:09',0,NULL,71,1),(845,'2020-02-17 17:15:09',0,NULL,72,1),(846,'2020-02-17 17:15:09',0,NULL,73,1),(847,'2020-02-17 17:15:09',0,NULL,51,1),(848,'2020-02-17 17:15:09',0,NULL,67,1),(849,'2020-02-17 17:15:09',0,NULL,55,1),(850,'2020-02-17 17:15:09',0,NULL,56,1),(851,'2020-02-17 17:15:09',0,NULL,60,1),(852,'2020-02-17 17:15:09',0,NULL,57,1),(853,'2020-02-17 17:15:09',0,NULL,58,1),(854,'2020-02-17 17:15:09',0,NULL,59,1),(855,'2020-02-17 17:15:09',0,NULL,61,1),(856,'2020-02-17 17:15:09',0,NULL,62,1),(857,'2020-02-17 17:15:09',0,NULL,63,1),(858,'2020-02-17 17:15:09',0,NULL,64,1),(859,'2020-02-17 17:15:09',0,NULL,65,1),(860,'2020-02-17 17:15:09',0,NULL,66,1);