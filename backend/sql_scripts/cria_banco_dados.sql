-- schema v0

-- DROP SCHEMA v0 CASCADE;

CREATE SCHEMA v0;


-- v0.estados definição

-- Drop table

-- DROP TABLE v0.estados;

CREATE TABLE v0.estados (
	id_estado varchar(2) NOT NULL,
	nome varchar(100) NOT NULL,
	regiao bpchar(1) NOT NULL,
	CONSTRAINT estados__id_pk PRIMARY KEY (id_estado)
);


-- v0.profissoes definição

-- Drop table

-- DROP TABLE v0.profissoes;

CREATE TABLE v0.profissoes (
	id_profissao serial4 NOT NULL,
	nome varchar(100) NOT NULL,
	nome_busca varchar(100) NOT NULL,
	data_inclusao timestamp DEFAULT now() NOT NULL,
	data_alteracao timestamp NULL,
	CONSTRAINT profissoes_nome_unique UNIQUE (nome_busca),
	CONSTRAINT profissoes_id_pk PRIMARY KEY (id_profissao)
);


-- v0.municipios definição

-- Drop table

-- DROP TABLE v0.municipios;

CREATE TABLE v0.municipios (
	id_estado varchar(2) NOT NULL,
	id_municipio serial4 NOT NULL,
	nome varchar(100) NOT NULL,
	CONSTRAINT municipios_id_pk PRIMARY KEY (id_municipio),
	CONSTRAINT municipios_estados_fk FOREIGN KEY (id_estado) REFERENCES v0.estados(id_estado) ON UPDATE RESTRICT
);
CREATE UNIQUE INDEX municipios_id_estado_idx ON v0.municipios USING btree (id_estado, id_municipio);


-- v0.usuarios definição

-- Drop table

-- DROP TABLE v0.usuarios;

CREATE TABLE v0.usuarios (
	id_usuario serial4 NOT NULL,
	nome varchar(100) NOT NULL,
	telefone varchar(20) NOT NULL,
	cpf_cnpj int4 NOT NULL,
	senha varchar(64) NOT NULL,
	email varchar(100) NOT NULL,
	id_municipio int4 NOT NULL,
	id_estado varchar(2) NOT NULL,
	bairro varchar(100),
	situacao bpchar(1) DEFAULT 'I'::bpchar NOT NULL,
	data_inclusao timestamp DEFAULT now() NOT NULL,
	data_alteracao timestamp NULL,
	bairro_busca varchar(100) NOT NULL,
	CONSTRAINT usuarios_cpf_cnpj_unique UNIQUE (cpf_cnpj),
	CONSTRAINT usuarios_email_unique UNIQUE (email),
	CONSTRAINT usuarios_id_pk PRIMARY KEY (id_usuario),
	CONSTRAINT usuarios_estados_fk FOREIGN KEY (id_estado) REFERENCES v0.estados(id_estado) ON UPDATE RESTRICT,
	CONSTRAINT usuarios_municipios_fk FOREIGN KEY (id_municipio) REFERENCES v0.municipios(id_municipio) ON UPDATE RESTRICT
);
CREATE INDEX usuarios_busca_idx ON v0.usuarios USING btree (situacao, id_estado, id_municipio, bairro);
CREATE INDEX usuarios_nome_idx ON v0.usuarios USING btree (nome);


-- v0.usuarios_profissoes definição

-- Drop table

-- DROP TABLE v0.usuarios_profissoes;

CREATE TABLE v0.usuarios_profissoes (
	id_usuario int4 NOT NULL,
	id_profissao int4 NOT NULL,
	situacao bpchar(1) NOT NULL,
	CONSTRAINT usuarios_profissoes_pk PRIMARY KEY (id_usuario, id_profissao),
	CONSTRAINT usuarios_profissoes_profissoes_fk FOREIGN KEY (id_profissao) REFERENCES v0.profissoes(id_profissao) ON UPDATE RESTRICT,
	CONSTRAINT usuarios_profissoes_usuarios_fk FOREIGN KEY (id_usuario) REFERENCES v0.usuarios(id_usuario) ON UPDATE RESTRICT
);


-- v0.servicos_prestados definição

-- Drop table

-- DROP TABLE v0.servicos_prestados;

CREATE TABLE v0.servicos_prestados (
	id_servicos_prestados serial4 NOT NULL,
	id_usuario_cliente int4 NOT NULL,
	id_usuario_prestador int4 NOT NULL,
	id_profissao int4 NOT NULL,
	avaliacao bpchar(1) NOT NULL,
	observacao text NULL,
	data_avaliacao timestamp NOT NULL,
	CONSTRAINT servicos_prestados_id_pk PRIMARY KEY (id_servicos_prestados),
	CONSTRAINT servicos_prestados_profissoes_fk FOREIGN KEY (id_profissao) REFERENCES v0.profissoes(id_profissao) ON UPDATE RESTRICT,
	CONSTRAINT servicos_prestados_usuarios_cliente_fk FOREIGN KEY (id_usuario_cliente) REFERENCES v0.usuarios(id_usuario) ON UPDATE RESTRICT,
	CONSTRAINT servicos_prestados_usuarios_prestador_fk FOREIGN KEY (id_usuario_prestador) REFERENCES v0.usuarios(id_usuario) ON UPDATE RESTRICT
);
CREATE INDEX servicos_prestados_id_usuario_cliente_idx ON v0.servicos_prestados USING btree (id_usuario_cliente, id_usuario_prestador, id_profissao);