import React, { Component } from 'react';


class FileInput extends Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.uploadSingleFile = this.uploadSingleFile.bind(this);
        this.fileInput = React.createRef();
        this.singleUploadForm = document.querySelector('#singleUploadForm');
        this.singleFileUploadInput = document.querySelector('#singleFileUploadInput');
        this.singleFileUploadError = document.querySelector('#singleFileUploadError');
        this.singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');
    }
    handleSubmit(event) {
        event.preventDefault();
        var files = singleFileUploadInput.files;
        if (files.length === 0) {
            singleFileUploadError.innerHTML = "Por favor seleccione un archivo PDF";
            singleFileUploadError.style.display = "block";
        }
        this.uploadSingleFile(files[0]);
        setTimeout(function () { this.props.history.push(`/verResultados/${encodeURI(files[0].name)}`) }.bind(this), 5000);
    }

    uploadSingleFile(file) {
        var formData = new FormData();
        formData.append("file", file);
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "api/uploadFile");

        xhr.onload = function () {
            console.log(xhr.responseText);
            var response = JSON.parse(xhr.responseText);
            if (xhr.status == 200) {
                singleFileUploadError.style.display = "none";
                singleFileUploadSuccess.innerHTML = "<p> El archivo ha subido correctamente. </p> <p> Porfavor Espere un momento</p>";
                singleFileUploadSuccess.style.display = "block";
            } else {
                singleFileUploadSuccess.style.display = "none";
                singleFileUploadError.innerHTML = (response && response.message) || "Algún Error Ocurrió";
            }
        }
        xhr.send(formData);
    }

    render() {
        return (
            <div style={{ color: "black" }}>
                <center>
                    <h3> Sistema de ayuda para la detección y corrección de errores de formato en trabajos académicos</h>
                </center>
                <form onSubmit={this.handleSubmit} id="singleUploadForm" name="singleUploadForm">
                    <label>
                        Suba su PDF:
                <input id="singleFileUploadInput" type="file" name="file" className="file-input" required ref={this.fileInput} />
                    </label>
                    <br />
                    <button type="submit">Enviar</button>
                </form>
                <div className="upload-response">
                    <div id="singleFileUploadError"></div>
                    <div id="singleFileUploadSuccess"></div>
                </div>
            </div>
        );
    }
}

export default FileInput;
