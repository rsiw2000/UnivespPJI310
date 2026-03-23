import json
import humps


class BaseTable(object):
    def __init__(self, *args, **kwargs) -> None:
        if args:
            self.set(args)
        elif kwargs:
            self.from_json(kwargs)

    def from_json(self, j: dict) -> None:
        for key, value in j.items():
            columns = [x.name for x in self.__table__.columns._all_columns]
            if key in columns:
                self.__setattr__(key, value)

    def to_json(self) -> dict:
        output: dict = {}
        for key in self.__annotations__.keys():
            output[key] = self.__dict__.get(key)
        return output
    
    def set(self, args):
        keys = [x.name for x in self.__table__.columns._all_columns if not x.primary_key]
        kw = dict(zip(keys, args))
        for key, value in kw.items():
            self.__setattr__(key, value)


class BaseRecord(object):
    def __init__(self, *args, **kwargs) -> None:
        if args:
            self.set(args)
        elif kwargs:
            self.from_json(kwargs)

    def from_json(self, j: dict) -> None:
        for key, value in j.items():
            camel_key = humps.pascalize(key)
            columns = self.__annotations__.keys()
            if camel_key in columns:
                self.__dict__[camel_key] = value

    def to_json(self) -> dict:
        output: dict = {}
        for key in self.__annotations__.keys():
            snake_key = humps.decamelize(key)
            output[snake_key] = self.__dict__.get(key)
        return output
    
    def set(self, args):
        keys = self.__annotations__.keys()
        kw = dict(zip(keys, args))
        for key, value in kw.items():
            self.__setattr__(key, value)

    
class BaseModel(list):
    data_class = None
    def __init__(self, data_class, filename=None,  *args, **kwargs):
        super(BaseModel, self).__init__(*args, **kwargs)
        self.data_class = data_class
        if filename:
            self.load(filename)

    def load(self, filename: str) -> None:
        with open(filename, "r", encoding="utf-8") as arq:
            try:
                content = json.load(arq)
                for item in content:
                    self.append(self.data_class(**item))
            except Exception as e:
                print ("ERROR", e)
