$(document).ready(function() {

    var DATE_REGEX = /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/;

    $.validator.addMethod(
        "regex",
        function(value, element, regexp) {
            return this.optional(element) || regexp.test(value);
        },
        "Please check your input."
    );

    $("#frmAddExperience").validate({
        onfocusout: function(element) {
            this.element(element);
        },
        onkeyup: false,
        onclick: false,
        rules: {
            companyName: {
                required: true,
            },
            projectName: {
                required: true,
            },
            language: {
                required: true,
            },
            framework: {
                required: true,
            },
            startDate: {
                required: true,
                regex: DATE_REGEX
            },
            endDate: {
                required: true,
                regex: DATE_REGEX
            },
            description: {
                required: true,
            }
        },
        messages: {
            companyName: {
                required: 'Company name cannot be empty.',
            },
            projectName: {
                required: 'Project name cannot be empty.',
            },
            language: {
                required: 'Language cannot be empty.',
            },
            framework: {
                required: 'Framework name cannot be empty.',
            },
            startDate: {
                required: 'Start work cannot be empty.',
                regex: 'Start work must be MM-dd-yyyy format'
            },
            endDate: {
                required: 'End work cannot be empty.',
                regex: 'End work must be MM-dd-yyyy format'
            },
            description: {
                required: 'Description cannot be empty.',
            }
        }
    });

    $( function() {
        var dateFormat = "mm/dd/yy",
            from = $( "#startDate" )
                .datepicker({
                    defaultDate: "+1w",
                    changeMonth: true,
                })
                .on( "change", function() {
                    to.datepicker( "option", "minDate", getDate( this ) );
                }),
            to = $( "#endDate" ).datepicker({
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