// The functions for displaying preview when the user upload head image
function preview1(file) {
    var img = new Image(), url = img.src = URL.createObjectURL(file);
    var $img = $(img);
    img.onload = function() {
        URL.revokeObjectURL(url);
        $('#preview').empty().append($img);
    }
}
function preview2(file) {
    var reader = new FileReader();
    reader.onload = function(e) {
        var $img = $('<img style="height:100px;width:100px;border:1;">').attr("src", e.target.result);
        $('#preview').empty().append($img);
    }
    reader.readAsDataURL(file);
}
 
$(function() {
    $('[type="file"]').change(function(e) {
        var file = e.target.files[0];
        preview2(file);
    });
});


// The function for count the upload pdf information and set the 'filename' value
function updateFileName() {
    var file = $('#pdfbutn').get(0).files[0];
    if (file) {
        // var fileSize = 0;
        // if (file.size > 1024 * 1024) 
        //     fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
        // else 
        //     fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';
        $('#filename').val(file.name);
    }
}


// The function validation the file type "pdf" only for now
function validPdfForm() {
    return true;
}

// Judage chinese
function isChinese(str){
    var reCh = /[u00-uff]/;
    return !reCh.test(str);
}

//Function for monitor the textarea
function introduceMonitor() {
    var strlen = 0;
    var txtval = $('#introduce').val();
    var txtlen = txtval.length;
    for (var i = 0; i < txtlen; i++) {
        if(isChinese(txtval.charAt(i)) == true)
            strlen = strlen + 2;
        else
            strlen = strlen + 1;
    }
    strlen = Math.ceil(strlen / 2);
    if ((140 - strlen) < 0)
        $('#submitBtn').attr("disabled", "true");
    else
        $('#submitBtn').removeAttr("disabled");
    $('em.resumewords').text(140 - strlen);
}


// Just for fun
$(function (){
    $('span.redBall').each(function (){
        $(this).text(Math.ceil((Math.random() * 100) % 34));
    });
    $('span.blueBall').text(Math.ceil((Math.random() * 100) % 17));
});


// Random color
function getColorByRandom(colorList){
    var colorIndex = Math.floor(Math.random() * colorList.length);
    var color = colorList[colorIndex];
    //colorList.splice(colorIndex, 1);
    return color;
} 

function rectCellRandomColor() {
    var colorList = ["#D6D6AD", "#B3D9D9", "#FFE4CA", "#D2E9FF", "#FFB5B5", "#FFE6FF"];
    $('.user-pdf-block').each(function() {       
        $(this).css("background-color", getColorByRandom(colorList));
    });
}

$(function() {
    rectCellRandomColor();
});