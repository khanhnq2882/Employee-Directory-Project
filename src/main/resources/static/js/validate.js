$(document).ready(function (){
    $("#frmAddEmployee").validate({
        rules: {
            email: {
                pattern: /^[a-zA-Z]{5,9}$/
            },
        },
        messages: {
            email: {
                pattern: 'Email is invalid.'
            }
        }
    })
})