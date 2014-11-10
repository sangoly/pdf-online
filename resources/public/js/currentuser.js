// The user page
$(function() {
    $('#userDetailPage .statuswords').click(function() {
        // First clear the error message
        $('#userDetailPage .statuserrors li').remove();
        var sta = $('#userDetailPage .statuswords');
        var text = sta.text();
        sta.html("<input type='text' id='statuswordsInput' value=" + text + " />");
        document.getElementById("statuswordsInput").focus();
        document.getElementById("statuswordsInput").addEventListener("blur", function(e) {
            var newStatusWords = document.getElementById("statuswordsInput").value;
            // just keep the words and wait the ajax update it
            sta.text(newStatusWords);
            $.get(context + "/user/status/update",
                  {"newStatusWords": newStatusWords},
                  function(response) {
                    var status = response.status;
                    switch (status) {
                        case "ok":
                            break;
                        case "fail":
                            $('#userDetailPage .statuserrors').html("<li>" + 
                                response.contents + "</li>");
                            sta.text(text);
                            break;
                        case "default":
                            sta.text(response.contents);
                            break;
                    }
                  },
                  "json");
        });
    });
});

function deletePdf(obj, userid, categoery, name) {
    var targetAddr = context + "/delete" + "/" + userid + "/" + categoery + "/" + name;
    $.get(targetAddr, {}, function (response) {
        if (response.status === "ok") {
            $(obj).parent().parent().remove();
            var tmp = $('.refUserCategoery ul li a.' + categoery).next();
            tmp.text(response.contents);
        } else 
            $('#userDetailPage .userPageError ul').append("<li>" + response.contents + "</li>");
    }, "json"); 
}