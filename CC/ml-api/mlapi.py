# https://cloud.google.com/storage/docs/reference/libraries#command-line


import os
from flask import Flask, flash, request, redirect, url_for, Response
from werkzeug.utils import secure_filename
from google.cloud import storage
from google.cloud import firestore
import uuid, shortuuid

UPLOAD_FOLDER = '/path/to/the/uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = 'tmp'
app.secret_key = 'entah'

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/detect', methods=['GET'])
def upload_file():
    if request.method == 'GET':
        filename = request.args.get('file')
        if(filename is None):
            return Response("{'Error':'there is no file part'}", status=400, mimetype='application/json')
        if(filename == ''):
            return Response("{'Error':'user does not select file'}", status=400, mimetype='application/json')        
        if(filename and allowed_file(filename)):
            print(filename)
            storage_client = storage.Client('qwiklabs-gcp-00-444fb8595efb')
            bucket_name = "qwiklabs-gcp-00-444fb8595efb"
            source_blob_name = filename
            destination_file_name = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            bucket = storage_client.bucket(bucket_name)
            blob = bucket.blob(source_blob_name)
            blob.download_to_filename(destination_file_name)
            print(
                "Blob {} downloaded to {}.".format(
                    source_blob_name, destination_file_name
                )
            ) 

            # Memanggil Program ML
            pecandu = True

            # Menyimpan hasil pengolahan ke nosql db
            db = firestore.Client()
            doc_ref = db.collection(u'hasil').document(u'{}'.format(filename))
            doc_ref.set({
                u'file': u'{}'.format(filename),
                u'pecandu': u'{}'.format(pecandu),
                u'akurasi': 50
            })
            
            return Response("{'Succes':'get success'}", status=200, mimetype='application/json')
    return Response("{'Error':'input does not match the rules.'}", status=400, mimetype='application/json')
        
        # check if the post request has the file part
    #     if 'file' not in request.files:
    #         flash('No file part')
    #         # return redirect(request.url)
    #         return Response("{'Error':'there is no file part'}", status=400, mimetype='application/json')
    #     file = request.files['file']
    #     # if user does not select file, browser also
    #     # submit an empty part without filename
    #     if file.filename == '':
    #         flash('No selected file')
    #         # return redirect(request.url)
    #         return Response("{'Error':'user does not select file'}", status=400, mimetype='application/json')
    #     if file and allowed_file(file.filename):
    #         newname = str(uuid.uuid4()) + '-' + str(shortuuid.uuid()) + '-' + file.filename
    #         filename = secure_filename(newname)
    #         file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

    #         storage_client = storage.Client('qwiklabs-gcp-00-444fb8595efb')
    #         bucket_name = "qwiklabs-gcp-00-444fb8595efb"
    #         source_file_name = os.path.join(app.config['UPLOAD_FOLDER'], filename)
    #         destination_blob_name = newname
    #         bucket = storage_client.bucket(bucket_name)
    #         blob = bucket.blob(destination_blob_name)
    #         blob.upload_from_filename(source_file_name)
    #         print(
    #             "File {} uploaded to {}.".format(
    #                 source_file_name, destination_blob_name
    #             )
    #         ) 

    #         return Response("{'Success':'file uploaded'}", status=200, mimetype='application/json')
    #         # return redirect(url_for('uploaded_file',
    #         #                         filename=filename))
    # return Response("{'Error':'input does not match the rules.'}", status=400, mimetype='application/json')
    # return '''
    # <!doctype html>
    # <title>Upload new File</title>
    # <h1>Upload new File</h1>
    # <form method=post enctype=multipart/form-data>
    #   <input type=file name=file>
    #   <input type=submit value=Upload>
    # </form>
    # '''