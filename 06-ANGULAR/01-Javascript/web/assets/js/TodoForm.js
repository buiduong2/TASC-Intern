export function TodoAdd(selectors, onSubmit, validate) {
    let btnEle;
    let inputEle;
    let messageEle;
    let valid = true;
    //init
    btnEle = document.querySelector(selectors.btnSelector);
    inputEle = document.querySelector(selectors.inputSelector);
    messageEle = document.querySelector(selectors.msgSelector);
    messageEle.style.display = 'none';
    // add Event
    inputEle.addEventListener('keydown', e => {
        if (e.key == 'Enter') {
            e.preventDefault();
            submit();
            return;
        }
        if (!valid) {
            valid = true;
            messageEle.style.display = 'none';
        }
    });
    btnEle.onclick = e => {
        e.preventDefault();
        submit();
    };
    // Function
    function submit() {
        const content = inputEle.value.trim();
        const validateMsg = validate(content);
        if (validateMsg) {
            valid = false;
            messageEle.style.display = 'block';
            messageEle.textContent = validateMsg;
        }
        if (validateMsg === null) {
            onSubmit(content);
            inputEle.value = '';
        }
    }
}
