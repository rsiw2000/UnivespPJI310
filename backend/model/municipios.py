from utils.utils import Utils

#IBGE7;UF;Município;Região
#1100015;RO;Alta Floresta D´oeste;Região Norte

class Municipio:
    Id: int
    Nome: str
    Regiao: str
    UF: str
    def __init__(self, id: int, uf: str, nome: str, regiao: str):
        self.Id = id
        self.UF = uf
        self.Nome = nome
        self.Regiao = regiao
        self.NormalNome = Utils.normalize(nome)
    def __str__(self):
        return f"{self.UF}  {self.Nome}  {self.Regiao}  {self.NormalNome}"


class Municipios(list):
    def __init__(self, *args, **kwargs):
        super(Municipios, self).__init__(*args, **kwargs)
        with open("C:/Users/Renato/FACULDADE/UnivespPJI310/backend/mockdata/municipios-ibge.csv", "r", encoding="utf-8") as arq:
            arq.readline() # pula linha de cabeçalho
            while True:
                linha = arq.readline()
                if not linha:
                    break
                dados = linha.split(";")
                self.append(Municipio(int(dados[0]), dados[1], dados[2], dados[3]))

    def get_nome(self, id: int) -> str:
        for municipio in self:
            if municipio.Id == id:
                return municipio.Nome

    def get_por_id(self, id: int) -> str:
        for municipio in self:
            if municipio.Id == id:
                return municipio

    def get_municipio(self, nome: str, uf: str) -> Municipio:
        nome = Utils.normalize(nome)
        for municipio in self:
            if nome and municipio.NormalNome == nome and uf == municipio.UF:
                return municipio
            
    def get_municipios_uf(self, uf: str) -> list:
        return [x for x in self if x.UF == uf]


def popula_tabela(municipios):
    with open("./sql_scripts/popula-municipios.sql", "w") as arq:
        for cid in municipios:
            line = f"INSERT INTO v0.municipios (id_municipio, id_estado, nome) VALUES ({cid.Id}, '{cid.UF}', '{cid.Nome}');\n"
            arq.write(line)
        arq.flush()

if __name__ == "__main__":
    municipios = Municipios()
    print(municipios.get_municipio("São Paulo", "SP"))
    print(municipios.get_municipio("Teresina", "PI"))
    for uf in ["SP", "RJ", "MG", "RS", "ES"]:
        print(uf, len(municipios.get_municipios_uf(uf)))
