CREATE TABLE clientes (idcliente int not null, nome varchar(100), cpf varchar(11) not null, email varchar(100), primary key(cpf));
CREATE TABLE lanchonetes (cnpj varchar(14) not null, nome varchar(100) not null, numerofunc int not null, primary key(cnpj));
CREATE TABLE pedidos (id int not null, lanche varchar(15) not null, adicional varchar(20) not null, preco real not null, primary key(id));

INSERT INTO clientes (idcliente, nome, cpf, email) VALUES (1, 'Luan', '12345678910', 'luan@gmail.com');
INSERT INTO clientes (idcliente, nome, cpf, email) VALUES (2, 'pedro', '11', 'pedro@gmail.com');
INSERT INTO clientes (idcliente, nome, cpf, email) VALUES (3, 'Rodrigo', '22', 'rodrigo@gmail.com');

INSERT INTO lanchonetes (cnpj, nome, numerofunc) VALUES ('12345678910111', 'Escritorio', 3);
INSERT INTO lanchonetes (cnpj, nome, numerofunc) VALUES ('11', 'Varanda', 10);
INSERT INTO lanchonetes (cnpj, nome, numerofunc) VALUES ('22', 'CasadoEspeto', 8);

INSERT INTO pedidos (id, lanche, adicional, preco) VALUES (1, 'X-Salada', 'Batata', 10.50);
INSERT INTO pedidos (id, lanche, adicional, preco) VALUES (2, 'X-Bacon', 'coca', 14);
INSERT INTO pedidos (id, lanche, adicional, preco) VALUES (3, 'X-tudo', 'Fanta', 20);
