import json
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.models import Sequential
from tensorflow.keras import layers
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image
import numpy as np
import cv2

model_json = open('model/model.json','r').read()
model = keras.models.model_from_json(model_json)
model.load_weights('model/model.h5')
CLASSIFIER_PATH = 'model/haarcascade_frontalface_alt2.xml'

def predict(foto):
  bgr_img = cv2.imread(foto)
  rgb_img = cv2.cvtColor(bgr_img, cv2.COLOR_BGR2RGB)
  gray_img = cv2.cvtColor(src=rgb_img, code=cv2.COLOR_RGB2GRAY)
  detector = cv2.CascadeClassifier(CLASSIFIER_PATH)
  detections = detector.detectMultiScale(image=gray_img)
  faces = sorted(tuple(data) for data in list(detections))
  faces = tuple(faces)

  img = image.load_img(foto, target_size=(180,180))
  x = image.img_to_array(img)
  x = np.expand_dims(x, axis=0)
  images = np.vstack([x])
  classes = model.predict_classes(images)

#  return classes[0]

  if len(faces)!=0:
    if classes<0.5:
      return("Addicted")
    else:
      return("Not Addicted")
  else:
    return("No Faces Detected")

	#i = image.load_img(foto, target_size=(180, 180))
	#i = image.img_to_array(i)
	#i = i.reshape(1, 180, 180, 3)
	#p = model.predict_classes(i)
	#print(p[0])
	#if p[0] > 0.5:
	#	return("Addicted")
	#else:
	#	return("Not Addicted")


