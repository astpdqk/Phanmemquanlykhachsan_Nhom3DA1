if OBJECT_ID('QLPhongKhachSan') is not null
drop database QLPhongKhachSan
go
create database QLPhongKhachSan
go

use QLPhongKhachSan
-- them table client
if OBJECT_ID('Client') is not null
drop table Client
go
create table Client
(id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
code varchar(20) unique not null,
[name] nvarchar(50) not null,
dateOfBirth date not null,
sex nvarchar(5) not null,
[address] nvarchar(200) not null,
idPersonCard varchar(12) unique not null,
customPhone varchar(10) 
)

-- table staff
if OBJECT_ID('Staff') is not null
drop table Staff
go
create table Staff
(id Uniqueidentifier primary key default newid(),
code varchar(20) unique not null,
[name] nvarchar(50) not null,
dateOfBirth date not null,
sex nvarchar(5) not null,
[address] nvarchar(200) not null,
idPersonCard varchar(12) unique not null,
phone varchar(10),
[user] varchar(30) unique  not null,
pass varchar(20)  not null,
[rule] varchar(20)  not null
)


--table bill
if OBJECT_ID('Bill') is not null
drop table Bill
go
create table bill
(
id Uniqueidentifier primary key default newid(),
idClient Uniqueidentifier ,
idStaff Uniqueidentifier,
code varchar(20) unique  not null,
Price DECIMAL(20,0) DEFAULT 0,
[status] int default 0,-- 0 là chưa thanh toán, 1 là đã thanh toán
[date] smalldatetime not null,
constraint FK_idClient foreign key (idClient ) references Client(id)ON DELETE CASCADE,
constraint FK_idStaff foreign key (idStaff ) references Staff(id)ON DELETE CASCADE,
)



--table item
if OBJECT_ID('Item') is not null
drop table Item
go
create table item
(id uniqueidentifier primary key default newid(),
code varchar(20)  not null,
[name] nvarchar(50)  not null
)

-- table promotionsService
if OBJECT_ID('promotionS') is not null
drop table promotionS
go
create table promotionS
(Id uniqueidentifier primary key default newid(),
code varchar(10) unique,
[value] decimal(20,0) default 0,
dateStart date  not null,
dateEnd date  not null,
)


--table service
if OBJECT_ID('service') is not null
drop table [service]
go
create table [service]
(
id uniqueidentifier primary key default newid(),
idpromotion uniqueidentifier  null,
code varchar(20) unique  not null,
[name] nvarchar(50)  not null,
price decimal(20,0) default 0,
[notes] nvarchar(50) ,
constraint fk_idpromotionS foreign key (idpromotion) references promotionS(id)ON DELETE CASCADE
)


-- table promotionROom
if OBJECT_ID('promotionR') is not null
drop table promotionR
go
create table promotionR
(Id uniqueidentifier primary key default newid(),
code varchar(10) unique  not null,
[value] decimal(20,0) default 0,
dateStart date  not null,
dateEnd date  not null,
)


--Table room
if OBJECT_ID('Room') is not null
drop table Room
go
create table Room
(id uniqueidentifier primary key default newid(),
[status] int default 1,-- 1 san sang, 2 co khach, 3 chua don, 4 dang don, 5 dang sua
KindofRoom int default 1,-- 1 phong don, 2 phong doi, 3 phong vip
idPromotion uniqueidentifier,
code varchar(20) unique  not null,
roomNumber varchar(10)  not null,
area varchar(10)  not null,
[location] nvarchar(50)  not null,
price decimal(20,0) default 0
constraint fk_idPromotion foreign key (idPromotion) references promotionR(id)ON DELETE CASCADE
)

-- Table roomItem
if OBJECT_ID('roomItem') is not null
drop table roomItem
go
create table roomItem
(roomId uniqueidentifier  not null,
itemId uniqueidentifier  not null,
[status] nvarchar(50)  not null,
amount int default 0
constraint pk_roomItem primary key (roomId,itemId),
constraint fk_roomid foreign key (roomid) references room(id)ON DELETE CASCADE,
constraint fk_itemid foreign key (itemid) references item(id)ON DELETE CASCADE
)


--Table roomBill
if OBJECT_ID('RoomBill') is not null
drop table RoomBill
go
create table RoomBill
(RoomId uniqueidentifier  not null,
BillId uniqueidentifier unique  not null,
priceRoom decimal(20,0) default 0,
promotionRoom decimal(20,0) default 0,
dateCheckIn smalldatetime  not null,
dateCheckOut smalldatetime not null
conStraint pk_roombill primary key(RoomId,BillId),
constraint fk_roomId1 foreign key (roomid) references room(id)ON DELETE CASCADE,
constraint fk_BillId foreign key (billid) references bill(id)ON DELETE CASCADE,
) 


--Table RoomBillService
if OBJECT_ID('RoomBillService') is not null
drop table RoomBillService
go
create table RoomBillService
(IdBill uniqueidentifier  not null,
IdService uniqueidentifier not null,
IdRoom uniqueidentifier not null,
priceService decimal(20,0) default 0,
promotionService decimal(20,0) default 0,
dateOfHire nvarchar(200) not null,
times int not null
constraint pk_roomBillService primary key(IdBill,IdService,IdRoom),
constraint fk_idBill foreign key (idbill) references roomBill(BillId)ON DELETE CASCADE,
constraint fk_idService foreign key (idservice) references [service](id)ON DELETE CASCADE,
)



 








