$(document).ready(function() {

    $("#frmAddSkill").validate({
        onfocusout: function(element) {
            this.element(element);
        },
        onkeyup: false,
        onclick: false,
        rules: {
            skillName: {
                required: true,
            },
            description: {
                required: true,
            }
        },
        messages: {
            skillName: {
                required: 'Skill name cannot be empty.',
            },
            description: {
                required: 'Description cannot be empty.',
            }
        }
    });

});