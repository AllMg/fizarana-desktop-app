class UFiles {

    constructor() {

    }

    reload() {
        console.log("call reload");
        $(".clickable").click(function (event) {
            var dataContainer = $(this).find(".hidden-info");
            var isFolder = dataContainer.attr("is-folder");
            var fullPath = dataContainer.attr("full-path");
            console.log("is-folder", isFolder, fullPath);
        });
    }

};

const ufiles = new UFiles();
ufiles.reload();