$(document).ready(function() {

    $("#frmAddDepartment").validate({
        onfocusout: function(element) {
            this.element(element);
        },
        onkeyup: false,
        onclick: false,
        rules: {
            departmentName: {
                required: true,
            },
            description: {
                required: true,
            }
        },
        messages: {
            departmentName: {
                required: 'Department name cannot be empty.',
            },
            description: {
                required: 'Description cannot be empty.',
            }
        }
    });

});