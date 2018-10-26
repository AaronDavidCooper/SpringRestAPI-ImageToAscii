/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

document.addEventListener('DOMContentLoaded', init);

function init(){
    document.getElementById('btnSubmit').addEventListener('click', upload);
}

function upload(ev) {
	
	ev.preventDefault();    //stop the form submitting
	
    var xhr = new XMLHttpRequest(),
        url = 'http://localhost:8080/ascii-rest/upload',
        fd = new FormData(),
        file = document.getElementById('upload_img');

    fd.append('file', file.files[0]);

    xhr.addEventListener('load', function () {
        console.log('Response:', this.responseText);
        var ImageAscii = JSON.parse(this.responseText);
        
        var chr = ImageAscii.pixelAscii;
        var hex = ImageAscii.hexColor;
        var width = ImageAscii.width;
        var html=[];
        
        html.push("<font face=\"Consolas\">");
        
        for(var i = 0; i < chr.length; i++){
        	html.push("<font color=\"" + hex[i] + "\">" + chr[i] + chr[i] + "</font>");
        	if(i != 0 && (i+1) % width == 0) {
        		html.push("<br>");
        	}
        }
        html.push("</font>");
        document.body.style.backgroundColor = "#000000";
       
        document.getElementById('result').innerHTML = html;
        
    });

    xhr.open('POST', url);
    xhr.send(fd);
}