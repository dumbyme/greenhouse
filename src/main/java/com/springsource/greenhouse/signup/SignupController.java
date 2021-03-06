/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.greenhouse.signup;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.social.web.signin.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.springsource.greenhouse.account.Account;
import com.springsource.greenhouse.account.AccountRepository;
import com.springsource.greenhouse.signup.SignupHelper.SignupCallback;

/**
 * UI Controller for signing up new members.
 * @author Keith Donald
 */
@Controller
public class SignupController {

	private final SignupHelper signupHelper;
	
	@Inject
	public SignupController(AccountRepository accountRepository, SignedUpGateway gateway) {
		this.signupHelper = new SignupHelper(accountRepository, gateway);
	}

	/**
	 * Render a signup form to the person as HTML in their web browser.
	 */
	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public SignupForm signupForm() {
		return new SignupForm();
	}

	/**
	 * Process a signup form submission.
	 * Delegate to a {@link SignupHelper} to actually complete the signin transaction.
	 * Redirects the new member to the application home page on successful sign-in.
	 */
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@Valid SignupForm form, BindingResult formBinding, final WebRequest request) {
		if (formBinding.hasErrors()) {
			return null;
		}
		boolean result = signupHelper.signup(form, formBinding, new SignupCallback() {
			public void postCreateAccount(Account account) {
				ProviderSignInUtils.handleConnectPostSignUp(account.getId(), request);
			}
		});
		return result ? "redirect:/" : null;
	}
}