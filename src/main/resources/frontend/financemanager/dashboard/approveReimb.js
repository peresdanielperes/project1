const approveReimb = async (e) => {
    e.preventDefault();
    let reimbId = e.target.id.slice(10);
    console.log(reimbId)

    let token = localStorage.getItem('token');

    const res = await fetch('http://localhost:9001/api/get/users/check-session',{
        headers:{
            'token': token
        }
    });

    const user = await res.json();

    const response = await fetch(`http://localhost:9001/api/put/reimbursements/approve/${user.id}/${reimbId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },

    });

    const data = await response.json();
    console.log(data);
    if(data === true){
        let status = document.getElementById(`status${reimbId}`);
        let approveBtn = document.getElementById(`approveBtn${reimbId}`);
        let denyBtn = document.getElementById(`denyBtn${reimbId}`);

        status.innerHTML = `<span id="status${reimbId}" class = "approved-status">APPROVED</span>`;
        approveBtn.style.visibility = "hidden";
        denyBtn.style.visibility = "hidden";
    }
}
