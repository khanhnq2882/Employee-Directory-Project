$(document).ready(function() {

    var DATE_REGEX = /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/;

    $.validator.addMethod(
        "regex",
        function(value, element, regexp) {
            return this.optional(element) || regexp.test(value);
        },
        "Please check your input."
    );

    $("#frmAddCertification").validate({
        onfocusout: function(element) {
            this.element(element);
        },
        onkeyup: false,
        onclick: false,
        rules: {
            certificationName: {
                required: true,
            },
            issuedDate: {
                required: true,
                regex: DATE_REGEX
            },
            expiredDate: {
                required: true,
                regex: DATE_REGEX
            },
            description: {
                required: true,
            }
        },
        messages: {
            certificationName: {
                required: 'Certification name cannot be empty.',
            },
            issuedDate: {
                required: 'Issued date cannot be empty.',
                regex: 'Issued date must be MM-dd-yyyy format'
            },
            expiredDate: {
                required: 'Expired date cannot be empty.',
                regex: 'Expired date must be MM-dd-yyyy format'
            },
            description: {
                required: 'Description cannot be empty.',
            }
        }
    });

    $( function() {
        var dateFormat = "mm/dd/yy",
            from = $( "#issuedDate" )
                .datepicker({
                    defaultDate: "+1w",
                    changeMonth: true,
                })
                .on( "change", function() {
                    to.datepicker( "option", "minDate", getDate( this ) );
                }),
            to = $( "#expiredDate" ).datepicker({
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
    } );

});