class UFiles {

    constructor() {
        this.url = window.location.href;
    }

    getFolderHistory(folderHistory) {
        var html = "";
        for (var i = 1; i < folderHistory.length; i++) {
            html += '<label class="clickable">' +
                '<span>' + folderHistory[i].pathName + ' ></span>' +
                '<span is-folder="' + folderHistory[i].isFolder + '" full-path="' + folderHistory[i].fullPath + '" class="hidden-info"></span>' +
                '</label>';
        }
        return html;
    }

    getFolderList(folderList) {
        var html = "";
        for (var i = 0; i < folderList.length; i++) {
            html += '<div class="col-md-4">' +
                '<div class="folder-file">';
            if (folderList[i].isFolder == "Y") {
                html += '<label class="clickable">';
            } else {
                html += '<label for="btn' + i + '">';
            }
            html += '<div class="row">' +
                '<div class="col-3">' +
                '<img src="' + folderList[i].iconName + '" class="a-icon" />' +
                '</div>' +
                '<div class="col-9" style="line-height: 1;">' +
                '<p class="f-name">' + folderList[i].folderName + '</p>' +
                '<p class="f-info"><i>' + folderList[i].folderInfo + '</i></p>' +
                '</div>' +
                '</div>';
            if (folderList[i].isFolder == "Y") {
                html += '<span is-folder="' + folderList[i].isFolder + '" full-path="' + folderList[i].fullPath + '" label-target="' + folderList[i].labelTarget + '" class="hidden-info"></span>';
            } else {
                html += '<form action="" method="POST" enctype="multipart/form-data" accept-charset="UTF-16" class="invisible">' +
                    '<input type="text" name="isFolder" value="' + folderList[i].isFolder + '" />' +
                    '<input type="text" name="fullPath" value="' + folderList[i].fullPath + '" />' +
                    '<button id="btn' + i + '"></button>' +
                    '</form>';
            }
            html += '</label>' +
                '</div>' +
                '</div>';
        }
        return html;
    }

    afterResponse(response) {
        var folderHistory = ufiles.getFolderHistory(response.folderHistory);
        var folderList = ufiles.getFolderList(response.folderList);
        $("#root-history").html(folderHistory);
        $("#folder-list").html(folderList);
        $(window).scrollTop(0);
        this.reload();
    }

    reload() {
        var url = this.url;
        ufiles = this;
        $(".clickable").click(function (event) {
            var dataContainer = $(this).find(".hidden-info");
            var isFolder = dataContainer.attr("is-folder");
            var fullPath = dataContainer.attr("full-path");
            var labelTarget = dataContainer.attr("label-target");
            if (labelTarget != undefined && labelTarget == "Modal") {
                $("#btnModal").click();
            } else {
                var formData = new FormData();
                formData.append("isFolder", isFolder);
                if (fullPath != undefined) {
                    formData.append("fullPath", fullPath);
                }
                $.ajax({
                    method: "POST",
                    url: url,
                    contentType: "multipart/form-data; charset=UTF-8",
                    processData: false,
                    data: formData
                }).done(function (response) {
                    ufiles.afterResponse(response);
                });
            }
        });
    }

    readFileData(file) {
        var ufiles = this;
        var filereader = new FileReader();
        filereader.onprogress = (e) => {
            console.log(e.loaded + " / " + e.total);
        };
        filereader.onload = (e) => {
            var arrayBuffer = e.target.result;
            var byteArray = new Int8Array(arrayBuffer);
            for(var i=0; i<byteArray.length; i++){
                if(byteArray[i] < -128 || byteArray[i] > 127) {
                    console.log(byteArray[i]);
                }
            }
            console.log("byteArray",byteArray);
            ufiles.sendFileData(byteArray, file.name);
        };
        filereader.readAsArrayBuffer(file);
    }

    sendFileData(byteArray, filename) {
        var formData = new FormData();
        formData.append("fileName", filename);
        formData.append("fileSize", byteArray.length);
        formData.append("byteArray", byteArray);
        var xhr = new XMLHttpRequest();
        xhr.responseType = "json";
        xhr.upload.onprogress = (e) => { console.log("Uploading", (e.loaded * 100 / e.total)); };
        xhr.upload.onload = () => { console.log("response",xhr.response); };
        xhr.upload.onabort = () => {
            console.error('Upload cancelled.');
        };
        xhr.upload.onerror = () => {
            console.error('Upload failed.');
        };
        xhr.open("POST", this.url);
        xhr.setRequestHeader("Content-Type", "application/octet-stream");
        xhr.send(formData);
    }

    upload() {
        var fileInput = document.getElementById("file-to-upload");
        console.log("fileInput", fileInput.files);
        if (fileInput.files.length > 0) {
            var file = document.getElementById("file-to-upload").files[0];
            console.log("file", file);
            /*var formData = new FormData();
            formData.append("isUpload", 1);
            formData.append("fileName", file.name);
            formData.append("file", file);*/
            this.readFileData(file);
            /*var xhr = new XMLHttpRequest();
            xhr.upload.onprogress = (e) => { console.log("Uploading", (e.loaded * 100 / e.total)); };
            xhr.upload.onload = () => { console.log(xhr.responseText); };
            xhr.upload.onabort = () => {
                console.error('Upload cancelled.');
            };
            xhr.upload.onerror = () => {
                console.error('Upload failed.');
            };
            xhr.open("POST", this.url);
            xhr.setRequestHeader("Content-Type", "application/octet-stream;" + file.name);
            xhr.setRequestHeader("Content-Length", file.size);
            xhr.send(file);*/
        }
    }

};

var ufiles = new UFiles();
ufiles.reload();
$("#btn-upload").click(function () {
    ufiles.upload();
});