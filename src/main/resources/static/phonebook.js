'use strict';

class EmployeeList extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			contacts: [],
			shown: []
		}
	}
	
	componentDidMount() {
		fetch('/getEntries').then(res => res.json()).then(
			(data) => {
				this.setState({ contacts: data, shown: data })
			}
		).catch(console.log)
	}
	
	handleChange = (e) => {
		var search = e.target.value.toLowerCase();
		const results = this.state.contacts.filter(person =>
			person.sn.toLowerCase().includes(search)
			|| person.givenName.toLowerCase().includes(search)
			|| (person.givenName.toLowerCase() + ' ' + person.sn.toLowerCase()).includes(search)
		);
		this.setState({shown: results})
	}
	
	generateResult () {
		return React.createElement("div", {key: 'userslist', id: 'userlist'},
			this.state.shown.map(
				function(listValue){
					return React.createElement("div", {key: listValue.sAMAccountName + "_userbox", className: 'userbox'},[

						React.createElement('div', {key: listValue.sAMAccountName + "_picture", className: 'userleft'}, [
							React.createElement('img', {key: listValue.sAMAccountName + "_avatar", className: 'avatar', src: 'data:image/jpg;base64,' + listValue.avatar}),
							React.createElement('div', {key: listValue.sAMAccountName + "_sAMAccountName", className: 'shortcut'}, listValue.sAMAccountName)
						]),
						React.createElement('div', {key: listValue.sAMAccountName + "_description", className: 'userright'},[
							React.createElement('div', {key: listValue.sAMAccountName + "_name", className: 'userprop'}, listValue.givenName + " " + listValue.sn),
							React.createElement('div', {key: listValue.sAMAccountName + "_telephoneNumber", className: 'userprop'}, listValue.telephoneNumber),
							React.createElement('div', {key: listValue.sAMAccountName + "_ipPhone", className: 'userprop'}, listValue.ipPhone),
							React.createElement('div', {key: listValue.sAMAccountName + "_mobile", className: 'userprop'}, listValue.mobile),
							React.createElement('div', {key: listValue.sAMAccountName + "_mail", className: 'userprop'}, listValue.mail),
							React.createElement('div', {key: listValue.sAMAccountName + "_company", className: 'userprop'}, listValue.company),
							React.createElement('div', {key: listValue.sAMAccountName + "_department", className: 'userprop'}, listValue.department),
							React.createElement('div', {key: listValue.sAMAccountName + "_title", className: 'userprop'}, listValue.title)
						]),
						React.createElement("div", {key: listValue.sAMAccountName + "_clearer", className: 'clear'})
					]
					);
				}
			)
		);
	}

	render() {
		console.log('Render is called.');
		return React.createElement('div', {id: 'container', key: 'container'}, [
			React.createElement('div', {id: 'searchcontainer', key: 'searchcontainer'},[
				React.createElement('input', {id: 'search', key: 'search', onChange: this.handleChange})
			]),
			this.generateResult()
		]);
	}
	
}

ReactDOM.render(
		React.createElement(EmployeeList, {id: 'employeelist', key: 'employeelist'}),
		document.getElementById('content')
);