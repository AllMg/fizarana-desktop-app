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

};

var ufiles = new UFiles();
ufiles.reload();