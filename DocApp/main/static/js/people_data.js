getPeopleInfo();

function getPeopleInfo(){
    var data = $.ajax( {
        type: 'GET',     
        url: 'https://covihack.pythonanywhere.com/api/profiles/',
        data: {},
        success: function(data) {
            var obj = JSON.parse;
            var x = data;
            // console.log(data);
            console.log(x);
            // ====================================================================================

              $.fn.dataTable.ext.errMode = 'none';
            $('#example').DataTable( {
                data: data,
                columns: [
                    { data: 'Mobile' },
                    {data: 'Probability'},
                    { data: 'ID' },
                ]
            } );

            // ==============================================================
        }


    });
    return data;
}