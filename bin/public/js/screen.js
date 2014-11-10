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


// Ajax post method to delete a categoery
function deleteCategoery() {
    $('.categoery-table a').click(function() {
        var categoery = $(this).parent().prev().prev().text();
        var anti = $('.add-categoery-block #__anti-forgery-token').val();
        var sel = this;
        $('.category-manage-outer .alert li[class="extra-info"]').remove();
        $.post(context + "/categoery/delete",
               {"categoery": categoery,
                "__anti-forgery-token": anti},
               function(response) {
                   if (response.status === "ok") {
                       $(sel).parent().parent().remove();
                       $('select#categoery').children().filter(function() {
                           return $(this).val() === categoery;
                       }).remove();
                   }
                   else
                       $(".category-manage-outer ul").append(
                         "<li class='extra-info'>删除分组错误</li>");
               },
               "json");
        return false;
    });
}
$(function() {
    deleteCategoery();
});

// Ajax post method to add new categoery
function addNewCategoery() {
    var newCategoery = $('.add-categoery-block #newCategoery').val();
    var anti = $('.add-categoery-block #__anti-forgery-token').val();
    // Clear the error message and the input value
    $('.category-manage-outer .alert li[class="extra-info"]').remove();
    $('.add-categoery-block input[type=text]').val("");
    $.post(context + "/categoery/add", 
           {"categoery": newCategoery,
            "__anti-forgery-token": anti}, 
            function(response) {
                if (response.status === "ok") {
                    $('.categoery-table table').append(
                        "<tr><td>" + newCategoery + 
                        "</td><td>0</td><td><a href='javascript:void(0)'>删除</a></td></tr>");
                    $('select[id=categoery]').append(
                        '<option class="selectItem">' + newCategoery + '</option>');
                }
                else if (response.status === "wrong")
                    $(".category-manage-outer ul").append(
                        "<li class='extra-info'>添加分组错误，请重新添加</li>");
                else if (response.status === "repeat")
                    $(".category-manage-outer ul").append(
                        "<li class='extra-info'>添加不成功，已存在分组</li>");
                else if (response.status === "null")
                    $(".category-manage-outer ul").append(
                        "<li class='extra-info'>不能添加空白分组</li>");
                else
                    $(".category-manage-outer ul").append(
                        "<li class='extra-info'>未知错误</li>");
                deleteCategoery();

            }, 
            "json");
}


// The function of adding comment
function addComment() {
    // first remove the error info
    $('.newComment .commerrors li').remove();
    var comment = $('.newComment textarea').val();
    var infoArray = $('.pdfInfo .col-md-7 .row');
    var pdfName = infoArray[0].getElementsByTagName('a')[0].innerText;
    var owner = infoArray[2].getElementsByTagName('div')[1].innerText;
    var categoery = infoArray[3].getElementsByTagName('div')[1].innerText; 
    var anti = $('.newComment #__anti-forgery-token').val();
    // remove the error info and input content
    $('.newComment .commerrors li').remove();
    $('.newComment #comment').val("");
    $.post(context + "/addComment",
           {"owner": owner,
            "categoery": categoery,
            "name": pdfName,
            "content": comment,
            "__anti-forgery-token": anti},
            function(response) {
                status = response.status;
                if (status === "unlogin") 
                    $('.newComment .commerrors').append("<li>请先登录</li>");
                else if (status === "ok")
                    $('.pdfComments .oldComment').append("<p>" + response.fromuser + 
                        "(" + response.timestamp + ")" + "</p>" +
                        "<p>" + response.content + "</p>" + 
                        "<hr style='border:1px solid blue;' />");
                else if (status === "error" || status === "warning")
                    $('.newComment .commerrors').append("<li>" + response.contents + "</li>");
            },
            "json");
}