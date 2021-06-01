# https://cloud.google.com/storage/docs/reference/libraries#command-line


import os
from flask import Flask, flash, request, redirect, url_for, Response
from werkzeug.utils import secure_filename
from google.cloud import storage
from google.cloud import firestore
from google.oauth2 import service_account
import requests
import json
import uuid, shortuuid

UPLOAD_FOLDER = '/path/to/the/uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = 'tmp'
app.secret_key = 'entah'

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/detect', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            # return redirect(request.url)
            return Response("{'Error':'there is no file part'}", status=400, mimetype='application/json')
        file = request.files['file']
        # if user does not select file, browser also
        # submit an empty part without filename
        if file.filename == '':
            flash('No selected file')
            # return redirect(request.url)
            return Response("{'Error':'user does not select file'}", status=400, mimetype='application/json')
        if file and allowed_file(file.filename):
            # Generate unique name
            newname = str(uuid.uuid4()) + '-' + str(shortuuid.uuid()) + '-' + file.filename
            filename = secure_filename(newname)

            # Save file ke direktori tmp
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

            # Upload ke GCS Bucket
            # credentials_bucket = service_account.Credentials.from_service_account_file("./bucket-sa-anarki-satujalan-b21-cap0463-17a1cb7858c0.json")
            # storage_client = storage.Client('anarki-satujalan-b21-cap0463', credentials=credentials_bucket)
            # storage_client = storage.Client(credentials=credentials)
            # bucket_name = "bucket-anarki-satujalan-b21-cap0463"
            # source_file_name = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            # destination_blob_name = newname
            # bucket = storage_client.bucket(bucket_name)
            # blob = bucket.blob(destination_blob_name)
            # blob.upload_from_filename(source_file_name)
            # print(
            #     "File {} uploaded to {}.".format(
            #         source_file_name, destination_blob_name
            #     )
            # )
            
            # Memanggil Program ML
            path_foto = "tmp/{}".format(newname)
            foto = open(path_foto, "rb")
            # hasil = namafunctionml(foto)
            # Dummy data
            pecandu = True
            akurasi = 86.7

            # Menyimpan hasil pengolahan ke nosql db
            # credentials_admin = service_account.Credentials.from_service_account_file("./admin-anarki-satujalan-b21-cap0463-5ee09b185288.json")
            # db = firestore.Client('anarki-satujalan-b21-cap0463', credentials=credentials_admin)
            # doc_ref = db.collection(u'hasil').document(u'{}'.format(filename))
            # doc_ref.set({
            #     u'file': u'{}'.format(filename),
            #     u'pecandu': u'{}'.format(pecandu),
            #     u'akurasi': akurasi
            # })
            
            # Hapus gambar
            os.system("rm {}".format(path_foto))

            result = {"file": str(filename), "pecandu": pecandu, "akurasi": akurasi}
            json_result = json.dumps(result)

            return Response(json_result, status=200, mimetype='application/json')

            # response = requests.get('http://mlapi-service:9000/detect?file={}'.format(newname))
            # data = response.text
            # print(data)
            
            # return Response("{'berhasil'}", status=200, mimetype='application/json')
            # return Response(data, status=200, mimetype='application/json')
            # return redirect(url_for('uploaded_file',
            #                         filename=filename))
    return Response("{'Error':'input does not match the rules.'}", status=400, mimetype='application/json')
    # return '''
    # <!doctype html>
    # <title>Upload new File</title>
    # <h1>Upload new File</h1>
    # <form method=post enctype=multipart/form-data>
    #   <input type=file name=file>
    #   <input type=submit value=Upload>
    # </form>
    # '''

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9000)
