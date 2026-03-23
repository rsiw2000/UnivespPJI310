from utils.utils import Utils

#IBGE;Estado;UF;Região;Qtd Mun;Sintaxe;;;
#11;Rondônia;RO;Região Norte;52;11'RO';;;"PROCV(A2;'Lista de Estados IBGE'!$A$2:$C$28;2;0)"

class Estado:
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
        

class Estados(list):
    def __init__(self, *args, **kwargs):
        super(Estados, self).__init__(*args, **kwargs)
        with open("C:/Users/Renato/FACULDADE/UnivespPJI310/backend/mockdata/estados-ibge.csv", "r", encoding="utf-8") as arq:
            arq.readline() # pula linha de cabeçalho
            while True:
                linha = arq.readline()
                if not linha:
                    break
                dados = linha.split(";")
                self.append(Estado(dados[0], dados[2], dados[1], dados[3]))

    def get_estado(self, nome: str = None, uf: str = None) -> Estado:
        if nome:
            nome = Utils.normalize(nome)
        for estado in self:
            if uf and estado.UF == uf.upper():
                return estado
            if nome and estado.NormalNome == nome:
                return estado


if __name__ == "__main__":
    estados = Estados()
    for estado in estados:
        print (estado)
    print(estados.get_estado(uf="SP"))
    print(estados.get_estado("Piauí"))
