import json
import keras
from keras.models import load_model
from keras.preprocessing import image
import numpy as np

model_json = open('model/model.json','r').read()
model = keras.models.model_from_json(model_json)
model.load_weights('model/model.h5')

def predict(foto):
	i = image.load_img(foto, target_size=(180, 180))
	i = image.img_to_array(i)
	i = i.reshape(1, 180, 180, 3)
	p = model.predict_classes(i)
	print(p[0])
	if p[0] > 0.5:
		return("Addicted")
	else:
		return("Not Addicted")

hasil = predict()
print(hasil)
