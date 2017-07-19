create database curso_inventario;
use curso_inventario;

create table usuario(
    id int not null auto_increment primary key,
    nombre varchar(70) ,
    clave varchar(150) ,
    fecha datetime,
    usuario_id int,
    tipo ENUM('cajero','administrador','supervisor') default 'cajero',
    visible boolean DEFAULT true
);

create table producto (
    id int not null auto_increment primary key,
    nombre varchar(190) unique,
    fecha datetime,
    usuario_id int,
    precio decimal(20,2) default 0,
    cantidad_almacen int default 0,
visible boolean DEFAULT true
);
create table persona(
    id int not null auto_increment primary key,
    nombre varchar(70) default '',
    apellido varchar(70) default '',
    cedula varchar(25) unique,
    fecha datetime,
    usuario_id int,
visible boolean DEFAULT true    
);
create table persona_direccion(
    id int not null auto_increment primary key,
    direccion text,
    fecha datetime,
    usuario_id int,
visible boolean DEFAULT true    
);
create table persona_email(
    id int not null auto_increment primary key,
    correo varchar(100),
    fecha datetime,
    usuario_id int,
visible boolean DEFAULT true    
);
create table tipo_telefono(
    id int not null auto_increment primary key,
    nombre varchar(100),
    fecha datetime,
    usuario_id int    ,
visible boolean DEFAULT true
);
insert into tipo_telefono (nombre,fecha,usuario_id) values('casa',now(),'1'),('celular',now(),'1'),('trabajo',now(),'1'),('whatsapp',now(),'1');
create table persona_telefono(
    id int not null auto_increment primary key,
    telefono varchar(100),
    tipo_telefono_id int,
    fecha datetime,
    usuario_id int    ,
visible boolean DEFAULT true
);
create table proveedor(
    id int not null auto_increment primary key,
    persona_id int,
    fecha datetime,
    usuario_id int  ,
visible boolean DEFAULT true
);
create table compra_producto (
    id int not null auto_increment primary key,
    producto_id int,
    proveedor_id int,
    fecha datetime,
    usuario_id int,
    precio_compra decimal(20,2),
    cantidad int,
visible boolean DEFAULT true
);
create table cliente(
    id int not null auto_increment primary key,
    persona_id int,
    rnc varchar(30),
    fecha datetime,
    usuario_id int  ,
visible boolean DEFAULT true
);
create table factura (
    id int not null auto_increment primary key,
    cliente_id int,
    fecha datetime,
    usuario_id int,
    ncf varchar(60),
    tiene_ncf boolean default false,
    sub_total decimal(20,2),
    monto_itbis decimal(20,2),
    monto_apagar decimal(20,2),
    estado enum('pendiente','pagada','cancelada'),
    cuadrada boolean default false,
    usuario_id_cuadre int,
    usuario_id_cancelado int,
    motivo_cancelado text,
visible boolean DEFAULT true
);
create table factura_detalle (
    id int not null auto_increment primary key,
    fecha datetime,
    precio decimal(20,2),
    monto decimal(20,2),
    cantidad int,
    factura_id int,
    produto_id int,
visible boolean DEFAULT true
);

create table generador_ncf(
    id int not null auto_increment primary key,
    fecha datetime,
    finalizo boolean default false,
    
    letra_serie_ncf_inicio char(5),
    letra_serie_ncf_fin char(5),
    
    division_negocio_ncf_inicio int(3),
    punto_emision_ncf_inicio int(4),
    area_impresion_ncf_inicio int(4),
    tipo_comprobante_fiscal_ncf_inicio int(3),
    secuencial_ncf_inicio bigint(4),
    
    division_negocio_ncf_fin int(3),
    punto_emision_ncf_fin int(4),
    area_impresion_ncf_fin int(4),
    tipo_comprobante_fiscal_ncf_fin int(3),
    secuencial_ncf_fin bigint(4),
    
    actual_secuencial_ncf bigint);