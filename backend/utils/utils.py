from hashlib import sha256
from unicodedata import normalize

class Utils(object):
    normalize_tokens = [' do ', ' da ', ' de ', ' em ', 'sao ', 'santa ', 'sta ', 'sr', 'sra', 'dona ', 'dna']
    @staticmethod
    def hash_token(username: str) -> str:
        seed = "@ppPrst@ç!23#99"
        return sha256((seed+username).encode()).hexdigest()

    @staticmethod
    def hash_password(username: str, password: str) -> str:
        seed = "@PpWd@Ç&44%#99"
        return sha256((password+seed+username).encode()).hexdigest()
    
    @staticmethod
    def get_action_if_valid(action: tuple, isLogged: bool, isAdmin: bool) -> tuple:
        if isLogged == action[2] and (isAdmin or not action[3]):
            return (action[0], action[1])

    @staticmethod
    def normalize(source: str) -> str:
        return normalize('NFKD', source).encode('ASCII','ignore').decode('ASCII').lower()

    @staticmethod 
    def normalized_search(source: str) -> str:
        normalized = Utils.normalize(source)
        for token in Utils.normalize_tokens:
            normalized = normalized.replace(token, '')
        normalized = normalized.replace(' ', '').replace('.', '').replace(',', '').replace(';', '')
        if normalized.endswith('s'):
            normalized = normalized[:-1]
        return normalized

    @staticmethod 
    def query_string_to_dict(query_string: str) -> dict:
        response = dict()
        params = query_string.split("&")
        for param in params:
            key, value = param.split('=',1)
            response[key]=value
        return response
    
    @staticmethod
    def safe_int(valor: any, default: int = None) -> int:
        try:
            return int(valor)
        except:
            return default
        
    @staticmethod
    def sanitize_doc(doc: str) -> int:
        try:
            return Utils.safe_int(str(doc).replace(",", "").replace(".", "").replace("-", "").replace("/", ""))
        except:
            return None


if __name__ == "__main__":
    print (Utils.hash_token("fulano"))
    print (Utils.hash_password("fulano","678901"))
