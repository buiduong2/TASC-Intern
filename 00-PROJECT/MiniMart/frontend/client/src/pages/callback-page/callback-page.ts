import { Component, OnInit } from '@angular/core';
import { saveCode } from '../../utils/auth/oauth.utils';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-callback-page',
  templateUrl: './callback-page.html',
})
export class CallbackPage implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
  ) {
    const code = this.route.snapshot.queryParamMap.get('code');
    if (code) {
      saveCode(code);
      setTimeout(() => {
        window.close();
      }, 1500);
    } else {
      // router.navigate(['/']);
    }
  }

  ngOnInit() {}
}
