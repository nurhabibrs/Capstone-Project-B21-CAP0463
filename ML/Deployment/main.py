import json
import keras
from keras.models import load_model
from keras.preprocessing import image
#import joblib

from flask import Flask, request

app = Flask(__name__)


model_json = open('model/model.json','r').read()
model = keras.models.model_from_json(model_json)
model.load_weights('model/model.h5')

@app.route("/predict", methods=["POST"])

def predict(img_dir):
	i = image.load_img(img_dir, target_size=(180, 180))
	i = image.img_to_array(i)
	i = i.reshape(1, 180, 180, 3)
	p = model.predict_classes(i)
	if p[0] > 0.5:
		return("Adicted")
	else:
		return("Not Adicted")

#	return dic[round(p[0])]

print(predict("M.jpeg"))

#if __name__ == '__main__':
#	app.run(host='0.0.0.0', port=5000)
