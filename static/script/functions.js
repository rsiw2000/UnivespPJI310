function postAndReloadIfSuccess(url, payload, error_callback)
{
    httpPostAsync(url, payload, reloadCurrentPage, error_callback);
}

function reloadCurrentPage()
{
    window.location.reload();
}

function httpGetAsync(theUrl, callback)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(JSON.parse(xmlHttp.responseText));
    }
    xmlHttp.onerror = function () {
        console.log("** An error occurred during the transaction");
        console.log(xmlHttp.responseText);
    };    
    xmlHttp.open("GET", theUrl, true); // true for asynchronous 
    xmlHttp.send(null);
}

function httpPostAsync(theUrl, payload, callback, error_callback = function() {})
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4)
            switch (xmlHttp.status){
                case 200:
                    callback(JSON.parse(xmlHttp.responseText));
                    break;
                default:
                    error_callback(xmlHttp.responseText);
        }
    }
    xmlHttp.onerror = function () {
        console.log("** An error occurred during the transaction");
        console.log(xmlHttp.responseText);
    };    
    xmlHttp.open("POST", theUrl, true); // true for asynchronous 
    xmlHttp.setRequestHeader('Content-Type', 'application/json');
    xmlHttp.send(JSON.stringify(payload));
}

