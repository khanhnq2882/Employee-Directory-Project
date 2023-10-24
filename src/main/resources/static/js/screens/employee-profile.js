$(document).ready(function() {
    $('#input-file').on('change', function() {
        if ($(this).get(0).files.length > 0) {
            $('#btn-upload').prop('disabled', false);
        } else {
            $('#btn-upload').prop('disabled', true);
        }
    });
});