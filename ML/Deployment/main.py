import json
import keras
import cv2
import numpy as np
from keras.models import load_model
from keras.preprocessing import image
#import joblib

from flask import Flask, request

app = Flask(__name__)


model_json = open('model/model.json','r').read()
model = keras.models.model_from_json(model_json)
model.load_weights('model/model.h5')
CLASSIFIER_PATH = 'model/haarcascade_frontalface_alt2.xml'

@app.route("/predict", methods=["POST"])

def predict(dir):
  bgr_img = cv2.imread(dir)
  rgb_img = cv2.cvtColor(bgr_img, cv2.COLOR_BGR2RGB)
  gray_img = cv2.cvtColor(src=rgb_img, code=cv2.COLOR_RGB2GRAY)
  detector = cv2.CascadeClassifier(CLASSIFIER_PATH)
  detections = detector.detectMultiScale(image=gray_img)
  faces = sorted(tuple(data) for data in list(detections))
  faces = tuple(faces)

  img = image.load_img(dir, target_size=(180,180))
  x = image.img_to_array(img)
  x = np.expand_dims(x, axis=0)
  images = np.vstack([x])
  classes = model.predict_classes(images)

#  return classes[0]

  if len(faces)!=0:
    if classes<0.5:
      return("Adicted")
    else:
      return("Not Adicted")
  else:
    return("No Faces Detected")

#	return dic[round(p[0])]

print(predict("Jidan2.jpeg"))

#if __name__ == '__main__':
#	app.run(host='0.0.0.0', port=5000)
