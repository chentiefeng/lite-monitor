var hostName = ''
window.onload=function(){
    loadDomain();
}
/**
 * 项目路径
 */
function loadDomain(){
    hostName = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port
}

function getUrl(url){
    return hostName + url
}