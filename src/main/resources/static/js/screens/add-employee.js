$(document).ready(function() {

    var EMAIL_REGEX = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
    var DATE_REGEX = /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/;
    var PHONENUMBER_REGEX = /^(0[3|5|7|8|9])+([0-9]{8})$/;

    $.validator.addMethod(
        "regex",
        function(value, element, regexp) {
            return this.optional(element) || regexp.test(value);
        },
        "Please check your input."
    );

    $("#frmAddEmployee").validate({
        onfocusout: function(element) {
            this.element(element);
        },
        onkeyup: false,
        onclick: false,
        rules: {
            email: {
                required: true,
                regex: EMAIL_REGEX
            },
            personalEmail: {
                required: true,
                regex: EMAIL_REGEX
            },
            firstName: {
                required: true,
            },
            lastName: {
                required: true,
            },
            dateOfBirth: {
                required: true,
                regex: DATE_REGEX
            },
            phoneNumber: {
                required: true,
                regex: PHONENUMBER_REGEX
            },
            address: {
                required: true,
            },
            coefficientsSalary: {
                required: true,
                regex: /^(\d+)?\.(\d+)?$/
            },
            startWork: {
                required: true,
                regex: DATE_REGEX
            },
            endWork: {
                required: true,
                regex: DATE_REGEX
            },

        },
        messages: {
            email: {
                required: 'Email cannot be empty.',
                regex: 'Please enter the correct email format'
            },
            personalEmail: {
                required: 'Personal email cannot be empty.',
                regex: 'Please enter the correct email format'
            },
            firstName: {
                required: 'First name cannot be empty.',
            },
            lastName: {
                required: 'Last name cannot be empty.',
            },
            dateOfBirth: {
                required: 'Date of birth cannot be empty.',
                regex: 'Date of birth must be MM-dd-yyyy format'
            },
            phoneNumber: {
                required: 'Phone number cannot be empty.',
                regex: 'Please enter the correct phone number format'
            },
            address: {
                required: 'Address cannot be empty.',
            },
            coefficientsSalary: {
                required: 'Coefficients salary cannot be empty.',
                regex: 'Please enter the correct coefficients salary format'
            },
            startWork: {
                required: 'Start work cannot be empty.',
                regex: 'Start work must be MM-dd-yyyy format'
            },
            endWork: {
                required: 'End work cannot be empty.',
                regex: 'End work must be MM-dd-yyyy format'
            },
        }
    });

    $( function() {

        $("#dateOfBirth").datepicker({
            defaultDate: "+1w",
            changeMonth: true,
        });
        var dateFormat = "mm/dd/yy",
            from = $( "#startWork" )
                .datepicker({
                    defaultDate: "+1w",
                    changeMonth: true,
                })
                .on( "change", function() {
                    to.datepicker( "option", "minDate", getDate( this ) );
                }),
            to = $( "#endWork" ).datepicker({
                defaultDate: "+1w",
                changeMonth: true,
            })
                .on( "change", function() {
                    from.datepicker( "option", "maxDate", getDate( this ) );
                });

        function getDate( element ) {
            var date;
            try {
                date = $.datepicker.parseDate( dateFormat, element.value );
            } catch( error ) {
                date = null;
            }

            return date;
        }
        if($('input:radio[name=gender]').is(':checked') === false) {
            $('input:radio[name=gender]').filter('[value=1]').prop('checked', true);
        }
        if($('input:radio[name=status]').is(':checked') === false) {
            $('input:radio[name=status]').filter('[value=1]').prop('checked', true);
        }
    } );

});