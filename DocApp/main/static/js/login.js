// console.log("check");
function check(event) {
    document.getElementById('message').innerHTML = "checking";
    const url = "https://covihack.pythonanywhere.com/auth/login/";
    const data = JSON.stringify({
         'username' : document.getElementById('username').value,
         'password' : document.getElementById('password').value,
        'email':  document.getElementById('email_id').value
     });
    console.log(data);
    const other_params = {
        headers : { "content-type" : "application/json; charset=UTF-8" },
        body : data,
        method : "POST",
        mode : "cors"
    };
    fetch(url, other_params)
        .then(function(response) {
            if (response.ok) {
                console.log(response.statusText);
                window.location.replace("/dashboard");   // This is just for testing, Remove during integrating
            } else {
                throw new Error("Could not reach the API: " + response.statusText);
            }
        }).then(function(data) {
            document.getElementById("message").innerHTML = data.encoded;
        }).catch(function(error) {
            document.getElementById("message").innerHTML = "";  //message 
        });
    return false;
}
