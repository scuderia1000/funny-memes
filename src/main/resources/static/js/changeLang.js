/**
 * Created by vvershov on 31.01.2020
 */
const select = document.getElementById("locales");
select.onchange = function(el) {
    const selectedOption = el.currentTarget.value,
        uri = window.location.protocol + '//' + window.location.host;
    if (selectedOption !== ''){
        window.location.replace(uri + '/?lang=' + selectedOption);
    }
};