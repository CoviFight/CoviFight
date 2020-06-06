var map;

var json;
var json_clone;
window.onload = function() {
	var url = 	'https://covihack.pythonanywhere.com/api/providers/';
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
		if ( this.readyState == 4 && this.status == 200 ) {
			var json = JSON.parse(xhttp.responseText);
      initialize(json);
console.log(json[0].name);
}}
xhttp.open("GET", url, true);
xhttp.send({'request': "authentication token"});
}
function initialize(json) {
  var mapOptions = {
    zoom: 11,
    center: new google.maps.LatLng( json[0].latitude, json[0].longitude)
  };
  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
  

  for(var i = 0; i < json.length; i++) {

    // Current objects
    var obj = json[i];
    var marker = new google.maps.Marker({
      position: new google.maps.LatLng(obj.latitude,obj.longitude),
      icon: {
        url:'../static/images/icon.png',

      },
      map: map,
      name: obj.name 
    });
    addClicker(marker, obj);
  }
  function addClicker(marker, content){
marker.addListener('click', function() {
  console.log(marker)
document.getElementById("details_name").innerHTML="Name:  " + content.name;
document.getElementById("details_lat").innerHTML="Latitude: " + content.latitude;
document.getElementById("details_long").innerHTML="Longitude: " + content.longitude;
});
  }
  
}