# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html


# useful for handling different item types with a single interface
from typing import List
from itemadapter import ItemAdapter
from pymongo import MongoClient
import logging
from scrapy.exceptions import DropItem
from scrapy.utils.project import get_project_settings
import json
from collections import defaultdict

settings = get_project_settings()

class DebugPipeline:
    def open_spider(self, spider):
        self.key = json.load(fp=open("kiwizzle_api.json", "r"))
        self.json_content = defaultdict()
        self.file = open('data2.json', 'w')

    def close_spider(self, spider):
        for key in self.json_content:
            self.json_content[key]["tech_stack"] = list(set(self.json_content[key]["tech_stack"]))
        json.dump(self.json_content, self.file, ensure_ascii=False)
        

    def process_item(self, item, spider):
        try:
            self.json_content[str(item["companyId"])]
        except KeyError:
            self.json_content[str(item["companyId"])] = {"companyName":"", "tech_stack": []}
            
        self.json_content[str(item["companyId"])]["companyName"] = self.key["company"][str(item["companyId"])]

        for language_key in item["language"]:
            self.json_content[str(item["companyId"])]["tech_stack"].append(self.key["language"][str(language_key)])

        return item

class JsonPipeline:
    def open_spider(self, spider):
        self.json_response = defaultdict()
        self.file = open('data.json', 'w')


    def close_spider(self, spider):
        for key in self.json_response:
            self.json_response[key]["tech_stack"] = list(set(self.json_response[key]["tech_stack"]))
        json.dump(self.json_response, self.file)
        self.file.close()


    def process_item(self, item, spider):
        if(item["flag"]):
            self.json_response[str(item["id"])] = {"companyName": "", "tech_stack":[]}
            logging.info(f"NAME : {item['name']}")
            self.json_response[str(item["id"])]["companyName"] = item["name"] 
            for tech in item["tech_stack"]:
                self.json_response[str(item["id"])]["tech_stack"].append(tech)
        else:
            for tech in item["tech_stack"]:
                self.json_response[str(item["id"])]["tech_stack"].append(tech)
    
        return item


class MongoDBPipeline:
    def open_spider(self, spider):
        self.json_response = defaultdict()
        connection = MongoClient(
            host=settings['MONGODB_SERVER'],
            port=settings['MONGODB_PORT'],
            username=settings['USERNAME'],
            password=settings['PASSWORD']
        )
        db = connection[settings['MONGODB_DB']]
        db[settings['MONGODB_COLLECTION'][0]].drop()
        self.collection = db[settings['MONGODB_COLLECTION'][0]] # set to programmers


    def close_spider(self, spider):
        for key in self.json_response:
            self.json_response[key]["tech_stack"] = list(set(self.json_response[key]["tech_stack"]))
            self.collection.insert(self.json_response[key])


    def process_item(self, item, spider):
        if(item["flag"]):
            self.json_response[str(item["id"])] = {"companyName": "", "tech_stack":[]}
            logging.info(f"NAME : {item['name']}")
            self.json_response[str(item["id"])]["companyName"] = item["name"] 
            for tech in item["tech_stack"]:
                self.json_response[str(item["id"])]["tech_stack"].append(tech)
        else:
            for tech in item["tech_stack"]:
                self.json_response[str(item["id"])]["tech_stack"].append(tech)
    
        return item
