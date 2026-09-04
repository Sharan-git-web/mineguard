package com.mineinspect.app.di

import javax.inject.Qualifier

/** Plain client (no auth header, no 401 authenticator) used only to call `/auth/refresh`
 *  itself — reusing the main authenticated client here would be circular. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshOkHttpClient
