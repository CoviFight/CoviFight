var map;
var json;
var json_clone;
window.onload = function() {
	var url = 	'https://covihack.pythonanywhere.com/api/highriskplaces';
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
    zoom: 12,
    center: new google.maps.LatLng( json[60].latitude, json[60].longitude)
  };
  map = new google.maps.Map(document.getElementById('map-hotspot'), mapOptions);
  for(var i = 0; i < json.length; i++) {
    var mag = 0.5*(i+1);
    // Current object
    var obj = json[i];
    var marker = new google.maps.Marker({
      position: new google.maps.LatLng(obj.latitude,obj.longitude),
      icon: {
        path: google.maps.SymbolPath.CIRCLE,
        scale: mag,
        fillColor: '#f00',
        fillOpacity: 0.35,
        strokeWeight: 0
      },
      map: map,
      name: obj.name 
    });
  }}